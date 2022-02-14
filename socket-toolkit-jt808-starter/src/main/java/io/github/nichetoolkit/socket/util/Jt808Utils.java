package io.github.nichetoolkit.socket.util;

import io.github.nichetoolkit.socket.constant.SocketJt808Constants;
import io.github.nichetoolkit.socket.manager.Jt808SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>RequestHelper</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class Jt808Utils {

    /**
     * 0x8100 终端注册应答
     * @param phoneBytes     sim卡号
     * @param flowIdBytes 终端对应信息流水
     * @param result         结果 0 成功 1 车辆已经被注册 2 数据库中无车辆 3 终端已被注册 4 数据库中无终端
     * @param auth           结果为0的时候的鉴权码
     * @return byte[] 返回应答
     */
    public static byte[] buildJt8100(byte[] phoneBytes, byte[] flowIdBytes, byte result, String auth) {
        byte[] bytes;
        /** 如果应答为0 则后缀鉴权码 否则不携带鉴权码 */
        if (result == 0) {
            bytes = ByteHexUtils.union(new byte[]{result}, auth.getBytes(SocketJt808Constants.GBK_CHARSET));
        } else {
            bytes = new byte[]{result};
        }
        return warp(
                new byte[]{(byte) 0x81, 0x00},
                phoneBytes,
                ByteHexUtils.union(flowIdBytes, bytes)
        );
    }

    /**
     * 0x8004 查询服务器时间应答 [v2019 新增]
     * @param phoneBytes sim卡号
     * @return byte[] 返回应答
     */
    public static byte[] buildJt8004(byte[] phoneBytes) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String timeHex = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        byte[] timeBytes = ByteHexUtils.parseByte(timeHex);
        return warp(
                new byte[]{(byte) 0x80, 0x04},
                phoneBytes,
                timeBytes
        );
    }

    /**
     * 0x8003 补传分包请求
     * @param phoneBytes   sim卡号
     * @param originFlowIdBytes 对应要求补传的原始消息第一包的流水号
     * @param size         重传包总数
     * @param idList       重传包id列表
     * @return byte[] 返回应答
     */
    public static byte[] buildJt8003(byte[] phoneBytes, byte[] originFlowIdBytes, byte size, byte[] idList) {
        return warp(
                new byte[]{(byte) 0x80, 0x03},
                phoneBytes,
                ByteHexUtils.union(originFlowIdBytes, new byte[]{size}, idList)
        );
    }

    /**
     * 0x8001 平台通用应答
     * @param phoneBytes        sim卡号
     * @param terminalFlowIdBytes    对应终端流水号
     * @param terminalMessageIdBytes 对应终端消息Id
     * @param body              应答参数 0 成功/确认 1 失败 2 消息有误 3 不支持 4 报警处理确认
     * @return byte[] 返回应答
     */
    public static byte[] buildJt8001(byte[] phoneBytes, byte[] terminalFlowIdBytes, byte[] terminalMessageIdBytes, byte body) {
        return warp(
                new byte[]{(byte) 0x80, 0x01},
                phoneBytes,
                ByteHexUtils.union(terminalFlowIdBytes, terminalMessageIdBytes, new byte[]{body}));
    }

    public static String parseDatetime(byte[] dateTime) {
        return ByteHexUtils.parseBcd(ByteHexUtils.subbyte(dateTime, 0, 1)) + "-" +
                ByteHexUtils.parseBcd(ByteHexUtils.subbyte(dateTime, 1, 2)) + "-" +
                ByteHexUtils.parseBcd(ByteHexUtils.subbyte(dateTime, 2, 3)) + " " +
                ByteHexUtils.parseBcd(ByteHexUtils.subbyte(dateTime, 3, 4)) + ":" +
                ByteHexUtils.parseBcd(ByteHexUtils.subbyte(dateTime, 4, 5)) + ":" +
                ByteHexUtils.parseBcd(ByteHexUtils.subbyte(dateTime, 5, 6));
    }

    public static String parseGBK(byte[] bytes) {
        return new String(bytes, SocketJt808Constants.GBK_CHARSET).trim();
    }

    public static String parseAscII(byte[] bytes){
        return new String(bytes, StandardCharsets.US_ASCII).trim();
    }

    /**
     * 判断否是 2019 版本 协议
     * 通过版本标识判断
     * @param bodyProps 消息体属性
     * @return boolean 是否是2019版本
     */
    public static boolean isVersion2019(byte[] bodyProps) {
        return (bodyProps[0] & 0x40) == 0x40;
    }

    /**
     * 检查是否是基本定位信息
     * @param location 位置信息
     * @return boolean
     */
    public static boolean verifyLocation(byte[] location) {
        /** 纬度[以度为单位的值乘以 10 的 6 次方，精确到百万分之一度] */ 
        byte[] latitude = ByteHexUtils.subbyte(location, 8, 12);
        /**  经度[以度为单位的值乘以 10 的 6 次方，精确到百万分之一度] */
        byte[] longitude = ByteHexUtils.subbyte(location, 12, 16);
        /** 高程 [单位 m] */
        byte[] height = ByteHexUtils.subbyte(location, 16, 18);
        /** 速度 [单位 0.1 km/h] */
        byte[] speed = ByteHexUtils.subbyte(location, 18, 20);
        /** 方向 [0~359 正北为0 顺时针] */
        byte[] direction = ByteHexUtils.subbyte(location, 20, 22);
        /** 时间 [yy-MM-dd-hh-mm-ss] */
        byte[] datetime = ByteHexUtils.subbyte(location, 22, 28);

        double longitudeDouble = (double)ByteHexUtils.parseFourInt(longitude) / (double)1000000;
        if(longitudeDouble > 180.0 || longitudeDouble < -180.0){
            return false;
        }
        double latitudeDouble = (double)ByteHexUtils.parseFourInt(latitude) / (double)1000000;
        if (latitudeDouble > 90.0 || latitudeDouble < -90.0) {
            return false;
        }
        int heightInt = ByteHexUtils.parseTwoInt(height);
        if (heightInt > 10000 || heightInt < 0) {
            return false;
        }
        double speedDouble = (double) (ByteHexUtils.parseTwoInt(speed)) / (double) 10;
        if (speedDouble < 0 || speedDouble > 600) {
            return false;
        }
        int directionInt = ByteHexUtils.parseTwoInt(direction);
        if (directionInt < 0 || directionInt > 359) {
            return false;
        }
        String month = ByteHexUtils.parseBcd(new byte[]{datetime[1]});
        int monthInt = Integer.parseInt(month);
        if (monthInt < 1 || monthInt > 12) {
            return false;
        }
        String day = ByteHexUtils.parseBcd(new byte[]{datetime[2]});
        int dayInt = Integer.parseInt(day);
        if (dayInt < 1 || dayInt > 31) {
            return false;
        }
        String hour = ByteHexUtils.parseBcd(new byte[]{datetime[3]});
        int hourInt = Integer.parseInt(hour);
        if (hourInt < 1 || hourInt > 24) {
            return false;
        }
        String minute = ByteHexUtils.parseBcd(new byte[]{datetime[4]});
        int minuteInt = Integer.parseInt(minute);
        if (minuteInt < 1 || minuteInt > 60) {
            return false;
        }
        String second = ByteHexUtils.parseBcd(new byte[]{datetime[5]});
        int secondInt = Integer.parseInt(second);
        if (secondInt < 1 || secondInt > 60) {
            return false;
        }
        return true;
    }

    /**
     * 获取消息体长度
     * @param bodyProps 消息体属性
     * @return int 消息体长度
     */
    public static int messageBodyLength(byte[] bodyProps) {
        int length = 0;
        if( bodyProps.length == SocketJt808Constants.NUMBER_2 ){
            int buf = 0x03ff;
            int body = ((bodyProps[0]<<8)&0xff00)^(bodyProps[1]&0x00ff);
            length = body&buf;
        }
        log.trace("body length is " + length);
        return length;
    }

    /**
     * 验证校验
     * @param bytes 需要验证的数据
     * @return boolean true 校验成功 false 校验失败
     */
    public static boolean verify(byte[] bytes) {
        boolean b = false;
        byte verify = bytes[0];
        for (int i = 1; i < bytes.length - 1; i++) {
            verify = (byte) (verify ^ bytes[i]);
        }
        if (verify == bytes[bytes.length - 1]) {
            b = true;
        }
        if (!b) {
            log.warn("verify code is " + ByteHexUtils.parseHex(verify) + " return " + b);
        }
        return b;
    }

    /**
     * 验证是否分包
     * @param bodyProps 消息体属性
     * @return 是否分包
     */
    public static boolean isSlicePackage(byte[] bodyProps) {
        if (bodyProps.length == SocketJt808Constants.NUMBER_2) {
            byte buffer = (byte) (bodyProps[0] & 0x20);
            return buffer != 0;
        } else {
            return false;
        }
    }

    /**
     * 转译还原
     * @param bytes 需要转义还原的数据
     * @return byte[] 转义还原后的数据
     */
    public static byte[] retrans(byte[] bytes) {
        byte[] buffer = ByteHexUtils.subbyte(bytes, 1, bytes.length - 1);
        for (int i = 0; i < buffer.length - 1; i++) {
            if (buffer[i] == 0x7d && buffer[i + 1] == 0x01) {
                buffer = ByteHexUtils.union(ByteHexUtils.subbyte(buffer, 0, i + 1), ByteHexUtils.subbyte(buffer, i + 2));
            } else if (buffer[i] == 0x7d && buffer[i + 1] == 0x02) {
                buffer = ByteHexUtils.union(
                        ByteHexUtils.subbyte(buffer, 0, i),
                        new byte[]{0x7e},
                        ByteHexUtils.subbyte(buffer, i + 2)
                );
            }
        }
        return buffer;
    }

    /**
     * 填充校验码
     * @param bytes 需要填充校验的消息数据
     * @return byte[] 填充校验后的数据
     */
    public static byte[] addVerify(byte[] bytes) {
        byte verify = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            verify = (byte) (verify ^ bytes[i]);
        }
        return ByteHexUtils.union(bytes, new byte[]{verify});
    }

    /**
     * 数据包转义
     * @param bytes 转义之前的数据
     * @return byte[] 转义之后的数据
     */
    public static byte[] trans(byte[] bytes) {
        for (int i = 0; i < bytes.length - 1; i++) {
            if (bytes[i] == 0x7d) {
                bytes = ByteHexUtils.union(
                        ByteHexUtils.subbyte(bytes, 0, i + 1),
                        new byte[]{0x01},
                        ByteHexUtils.subbyte(bytes, i + 1)
                );
            } else if (bytes[i] == 0x7e) {
                bytes = ByteHexUtils.union(
                        ByteHexUtils.subbyte(bytes, 0, i),
                        new byte[]{0x7d, 0x02},
                        ByteHexUtils.subbyte(bytes, i + 1)
                );
            }
        }
        bytes = ByteHexUtils.union(new byte[]{0x7e}, bytes);
        bytes = ByteHexUtils.union(bytes, new byte[]{0x7e});
        return bytes;
    }

    /**
     * 包装返回值
     * @param messageId 消息ID
     * @param phoneBytes 电话
     * @param total 分包总数
     * @param count 分包序号 从1开始
     * @param flowId 平台流水号
     * @param messageBody 消息体
     * @return 返回值
     */
    public static byte[] warp (byte[] messageId, byte[] phoneBytes,int total, int count, int flowId, byte[] messageBody) {
        int bodyLen = messageBody.length;
        if (phoneBytes.length == 10) {
            // 2019
            return ByteHexUtils.union(
                    messageId,
                    new byte[]{(byte)(((bodyLen>>>8) & 0x03) | 0x40),(byte) (bodyLen&0xff), 0x01},
                    phoneBytes,
                    new byte[]{(byte) ((flowId>>>8)&0xff),(byte) (flowId&0xff)},
                    new byte[]{(byte) ((total>>>8)&0xff),(byte) (total&0xff)},
                    new byte[]{(byte) ((count>>>8)&0xff),(byte) (count&0xff)},
                    messageBody);
        } else {
            // 2011 2013
            return ByteHexUtils.union(
                    messageId,
                    new byte[]{(byte)((bodyLen>>>8) & 0x03),(byte) (bodyLen&0xff)},
                    phoneBytes,
                    new byte[]{(byte) ((flowId>>>8)&0xff),(byte) (flowId&0xff)},
                    new byte[]{(byte) ((total>>>8)&0xff),(byte) (total&0xff)},
                    new byte[]{(byte) ((count>>>8)&0xff),(byte) (count&0xff)},
                    messageBody);
        }
    }

    /**
     * 包装命令
     * @param messageId   消息编号
     * @param phoneBytes  手机号包
     * @param messageBody 消息体
     * @return byte[] 命令
     */
    public static byte[] warp(byte[] messageId, byte[] phoneBytes, byte[] messageBody) {
        int bodyLength = messageBody.length;
        int flowId = parseFlowId(phoneBytes);
        if (phoneBytes.length == 10) {
            // 2019
            return ByteHexUtils.union(
                    messageId,
                    new byte[]{(byte) (((bodyLength >>> 8) & 0x03) | 0x40), (byte) (bodyLength & 0xff), 0x01},
                    phoneBytes,
                    new byte[]{(byte) ((flowId >>> 8) & 0xff), (byte) (flowId & 0xff)},
                    messageBody);
        } else if (phoneBytes.length == 6) {
            // 2011 2013
            return ByteHexUtils.union(
                    messageId,
                    new byte[]{(byte) ((bodyLength >>> 8) & 0x03), (byte) (bodyLength & 0xff)},
                    phoneBytes,
                    new byte[]{(byte) ((flowId >>> 8) & 0xff), (byte) (flowId & 0xff)},
                    messageBody);
        } else {
            return null;
        }
    }

    /**
     * 包装命令
     * @param messageId  消息编号
     * @param phoneBytes 手机号包
     * @return byte[] 命令
     */
    public static byte[] warp(byte[] messageId, byte[] phoneBytes) {
        int flowId = parseFlowId(phoneBytes);
        if (phoneBytes.length == 10) {
            // 2019
            return ByteHexUtils.union(
                    messageId,
                    new byte[]{0x40, 0x00, 0x01},
                    phoneBytes,
                    new byte[]{(byte) ((flowId >>> 8) & 0xff), (byte) (flowId & 0xff)});
        } else if (phoneBytes.length == 6) {
            // 2011 2013
            return ByteHexUtils.union(
                    messageId,
                    new byte[]{0x00, 0x00},
                    phoneBytes,
                    new byte[]{(byte) ((flowId >>> 8) & 0xff), (byte) (flowId & 0xff)});
        } else {
            return null;
        }
    }

    /**
     * 获取流水号
     * @param phoneBytes 手机号包
     * @return int 流水号
     */
    public static int parseFlowId(byte[] phoneBytes) {
        return parseFlowId(phoneBytes, 1);
    }

    /**
     * 获取流水号
     * @param phoneBytes 手机号包
     * @param index      脚标序列
     * @return int 流水号
     */
    public static int parseFlowId(byte[] phoneBytes, int index) {
        String phone = ByteHexUtils.parseHex(phoneBytes);
        Object session = Jt808SessionManager.get(phone);
        if (session == null) {
            return 0;
        }
        int ret = 0;
        if (session instanceof ChannelHandlerContext) {
            ChannelHandlerContext ctx = (ChannelHandlerContext) session;
            Object flowId = ctx.channel().attr(AttributeKey.valueOf(SocketJt808Constants.FLOW_ID)).get();
            if (flowId != null) {
                ret = (int) flowId;
            }
            ctx.channel().attr(AttributeKey.valueOf(SocketJt808Constants.FLOW_ID)).set(ret + index);
        }
        return ret;
    }
}
