package io.github.nichetoolkit.socket.manager;

import io.github.nichetoolkit.rest.RestException;
import io.github.nichetoolkit.rest.util.GeneralUtils;
import io.github.nichetoolkit.socket.configure.SocketJt808Properties;
import io.github.nichetoolkit.socket.constant.SocketJt808Constants;
import io.github.nichetoolkit.socket.model.Jt808Message;
import io.github.nichetoolkit.socket.server.handler.SocketHandlerManager;
import io.github.nichetoolkit.socket.server.handler.SocketMessageHandler;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808CacheService;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Jt808MessageHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
@Component
public class Jt808MessageHandler implements SocketMessageHandler {

    @Autowired
    private Jt808CacheService cacheService;

    @Autowired
    private SocketJt808Properties jt808Properties;


    @Override
    public void beforeMessage(Object jt808Message) throws RestException {

    }

    @Override
    public void doHandle(Object session, Object message) throws RestException {
        byte[] messageBytes = (byte[]) message;
        Jt808Message jt808Message = parseMessage(messageBytes);
        handleSession(jt808Message, session);
        handleMessage(session, jt808Message);
    }

    @Override
    public void afterSession(Object session) throws RestException {

    }

    public void handleMessage(Object session, Jt808Message jt808Message) throws RestException {
        byte[] response = null;
        String phone = jt808Message.getPhone();
        byte[] originDataBytes = jt808Message.getOriginDataBytes();
        if (jt808Message.isSlicePackage()) {
            int totalPackageSize = ByteHexUtils.parseTwoInt(jt808Message.getPackageSizeBytes());
            int currentPackageIndex = ByteHexUtils.parseTwoInt(jt808Message.getPackageIndexBytes());
            /** 序号必须小于等于总包数 条件达成之后进行分包处理 否则不处理分包且不处理数据 */
            if (totalPackageSize >= currentPackageIndex) {
                if (!cacheService.containsPackages(phone)) {
                    ConcurrentHashMap<Integer, byte[]> packageBufferMap = new ConcurrentHashMap<>(totalPackageSize);
                    cacheService.setPackages(phone, packageBufferMap);
                }
                Map<Integer, byte[]> packageBufferMap = cacheService.getPackages(phone);
                if (packageBufferMap != null) {
                    packageBufferMap.put(currentPackageIndex, originDataBytes);
                }
            }
            /** 分包结束时需要对分包数据进行解析处理并返回应答 通过总包数和序号对比 判断是不是最后一包 */
            if (totalPackageSize == currentPackageIndex) {
                /** 如果是 这个电话的最后一包 */
                if (isFinishPackage(phone, totalPackageSize)) {
                    /** 合并所有包 并解析 */
                    byte[] bytes = mergePackage(phone, totalPackageSize);
                    if (GeneralUtils.isNotEmpty(bytes)) {
                        response = handlePackage(bytes);
                    }
                } else {
                    /** 没有全部收到 需要补传 最初一包的流水号 */
                    byte[] flowId = null;
                    /** 补传id列表 */
                    byte[] idList = new byte[]{};
                    /** 补传数量 */
                    byte count = 0;
                    Map<Integer, byte[]> packageBufferMap = cacheService.getPackages(phone);
                    if (GeneralUtils.isNotEmpty(packageBufferMap)) {
                        for (int i = 1; i <= totalPackageSize; i++) {
                            if (GeneralUtils.isEmpty(flowId)) {
                                if (jt808Message.isVersion2019()) {
                                    flowId = ByteHexUtils.subbyte(packageBufferMap.get(1), 15, 17);
                                } else {
                                    flowId = ByteHexUtils.subbyte(packageBufferMap.get(1), 10, 12);
                                }
                            }
                            if (!packageBufferMap.containsKey(i)) {
                                count++;
                                if (jt808Message.isVersion2019()) {
                                    idList = ByteHexUtils.union(idList, ByteHexUtils.subbyte(packageBufferMap.get(i), 19, 21));
                                } else {
                                    idList = ByteHexUtils.union(idList, ByteHexUtils.subbyte(packageBufferMap.get(i), 14, 16));
                                }
                            }
                        }
                    }
                    if (GeneralUtils.isNotEmpty(flowId)) {
                        response = Jt808Utils.buildJt8003(jt808Message.getPhoneBytes(), flowId, count, idList);
                    }
                }
            }
        } else {
            response = handlePackage(originDataBytes);
        }
        if (response == null) {
            response = Jt808Utils.buildJt8001(jt808Message.getPhoneBytes(), jt808Message.getFlowIdBytes(), jt808Message.getMessageIdBytes(), (byte) 0x00);
        }
        /** 分包消息总长度 */
        int packageLength = SocketJt808Constants.PACKAGE_LENGTH;
        if (response.length > packageLength) {
            /** 分包发送 */
            sentPackage(response, Jt808SessionManager.get(phone));
        } else {
            if (session instanceof IoSession) {
                ((IoSession) Jt808SessionManager.get(phone)).write(response);
            } else if (session instanceof ChannelHandlerContext) {
                ((ChannelHandlerContext) Jt808SessionManager.get(phone)).writeAndFlush(response);
            }
        }
    }

    public byte[] handlePackage(byte[] messageBytes) throws RestException {
        byte[] messageBodyBytes;

        int offset = 0;

        final byte[] messageIdBytes = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        final byte[] messageBodyPropertyBytes = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        /** 通过消息体属性中的版本标识位 判断是否是 2019版本协议 并增加相关解析 */
        byte[] phoneBytes;
        if (Jt808Utils.isVersion2019(messageBodyPropertyBytes)) {
            /** 忽略 协议版本解析 */
            offset++;
            phoneBytes = new byte[]{
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++],
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++]
            };
        } else {
            phoneBytes = new byte[]{
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++],
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++]
            };
        }
        final byte[] flowIdBytes = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        int messageLength = SocketJt808Constants.PACKAGE_LENGTH;
        if (messageBytes.length > messageLength) {
            /** 超长的数据一定是分包合并后的数据 直接获取后边的数据即可 因为已经处理了尾部的校验位 */
            /** 过滤掉消息包封装项 */
            offset += 4;
            messageBodyBytes = ByteHexUtils.subbyte(messageBytes, offset);
        } else {
            int bodyLength = messageBytes.length - 1 - offset;
            messageBodyBytes = new byte[bodyLength];
            for (int i = 0; i < messageBodyBytes.length; i++) {
                messageBodyBytes[i] = messageBytes[offset++];
            }
        }
        String phone = ByteHexUtils.parseHex(phoneBytes);
        int messageId = ByteHexUtils.parseTwoInt(messageIdBytes);
        /** 检查鉴权记录 看是否链接后鉴权过 成功鉴权才能继续访问其他命令 */
        /** 每次都判断终端鉴权 有些短连接如果每次鉴权 的话 很麻烦 所以推荐使用长链接Map */
        boolean isAuth = cacheService.containsAuth(phone);
        if (jt808Properties.getAuth()) {
            /** 如果未鉴权 则可以使用 authMsgId 中定义的命令 */
            if (!isAuth) {
                String[] authIds = jt808Properties.getAuthIds();
                for (String authId : authIds) {
                    if (authId.length() == 4 && messageId == ByteHexUtils.parseTwoInt(ByteHexUtils.parseByte(authId))) {
                        isAuth = true;
                        break;
                    }
                }
            }
        } else {
            /** 不需要检查权限 直接设置为true 已授权 */
            isAuth = true;
        }
        if (!isAuth) {
            /** 鉴权失败直接返回 */
            return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x01);
        }
        SocketPackageHandler packageHandler = SocketHandlerManager.handler(messageId);
        return packageHandler.handle(phoneBytes, flowIdBytes, messageIdBytes, messageBodyBytes);
    }

    public void sentPackage(byte[] bytes, Object session) {
        byte[] messageBodyPropertyBytes = new byte[]{bytes[2], bytes[3]};
        boolean isVersion2019 = Jt808Utils.isVersion2019(messageBodyPropertyBytes);
        /** 完整的消息体 */
        byte[] messageBodyBytes = ByteHexUtils.subbyte(bytes, 12);
        /** 包数量 */
        int packageSize = messageBodyBytes.length % 1023 == 0 ? messageBodyBytes.length / 1023 : messageBodyBytes.length / 1023 + 1;

        /** 消息Id */
        byte[] messageIdBytes = ByteHexUtils.subbyte(bytes, 0, 2);
        /** Sim卡号 */
        byte[] phoneBytes;
        if (isVersion2019) {
            phoneBytes = ByteHexUtils.subbyte(bytes, 5, 15);
        } else {
            phoneBytes = ByteHexUtils.subbyte(bytes, 4, 10);
        }
        /** 平台流水号 直接获取对应包数量的流水号 */
        int flowId = Jt808Utils.parseFlowId(phoneBytes, packageSize);

        /** 存储下发数据 */
        String phone = ByteHexUtils.parseHex(phoneBytes);
        Map<Integer, byte[]> sentPackages = cacheService.getSentPackages(phone);
        if (sentPackages == null) {
            cacheService.setSentPackages(phone, new HashMap<>());
            sentPackages = cacheService.getSentPackages(phone);
        } else {
            sentPackages.clear();
        }

        for (int i = 1; i <= packageSize; i++) {
            byte[] bodyBufferBytes;
            if (packageSize == i) {
                bodyBufferBytes = ByteHexUtils.subbyte(messageBodyBytes, (i - 1) * 1023);
            } else {
                bodyBufferBytes = ByteHexUtils.subbyte(messageBodyBytes, (i - 1) * 1023, i * 1023);
            }
            byte[] data = Jt808Utils.warp(
                    messageIdBytes,
                    phoneBytes,
                    packageSize,
                    i,
                    flowId,
                    bodyBufferBytes
            );
            sentPackages.put(i, data);
            if (session instanceof IoSession) {
                ((IoSession) session).write(data);
            } else if (session instanceof ChannelHandlerContext) {
                ((ChannelHandlerContext) session).writeAndFlush(data);
            }
            flowId++;
        }
    }

    public byte[] mergePackage(String phone, int totalPackageSize) {
        Map<Integer, byte[]> packageBufferMap = cacheService.getPackages(phone);
        if (GeneralUtils.isEmpty(packageBufferMap)) {
            return new byte[0];
        }
        /** 分包是从1开始的 去掉校验位 */
        byte[] bytes = ByteHexUtils.subbyte(packageBufferMap.get(1), 0, packageBufferMap.get(1).length - 1);
        for (int i = 2; i <= totalPackageSize; i++) {
            byte[] pkg = packageBufferMap.get(i);
            bytes = ByteHexUtils.union(bytes, ByteHexUtils.subbyte(pkg, phone.length() == 20 ? 21 : 16, packageBufferMap.get(1).length - 1));
        }
        packageBufferMap.clear();
        return bytes;
    }


    public boolean isFinishPackage(String phone, int totalPackageSize) {
        Map<Integer, byte[]> packageBufferMap = cacheService.getPackages(phone);
        if (packageBufferMap == null) {
            return false;
        }
        return cacheService.containsPackages(phone) && packageBufferMap.size() == totalPackageSize;
    }

    public void handleSession(Jt808Message jt808Message, Object session) throws RestException {
        /** 相同身份的终端建立链接 原链接需要断开 也就是加入之前需要判断是否存在终端 存在关闭后在加入 */
        /** 平台流水号 绑定到 session 一直增加即可 */
        String phone = jt808Message.getPhone();
        int flowId;
        if (Jt808SessionManager.contains(phone)) {
            if (session instanceof IoSession) {
                IoSession nextSession = (IoSession) session;
                IoSession prevSession = (IoSession) Jt808SessionManager.get(phone);
                flowId = (int) prevSession.getAttribute(SocketJt808Constants.FLOW_ID);
                if (prevSession.getId() != nextSession.getId()) {
                    prevSession.closeNow();
                }
                nextSession.setAttribute(phone, flowId);

            } else if (session instanceof ChannelHandlerContext) {
                ChannelHandlerContext nextSession = (ChannelHandlerContext) session;
                ChannelHandlerContext prevSession = (ChannelHandlerContext) Jt808SessionManager.get(phone);
                flowId = (int) prevSession.channel().attr(AttributeKey.valueOf(SocketJt808Constants.FLOW_ID)).get();
                if (!prevSession.name().equals(nextSession.name())) {
                    prevSession.close();
                }
                nextSession.channel().attr(AttributeKey.valueOf(SocketJt808Constants.FLOW_ID)).set(flowId);
            }

        }
        Jt808SessionManager.set(phone, session);

    }

    public Jt808Message parseMessage(byte[] messageBytes) throws RestException {
        Jt808Message jt808Message = new Jt808Message();
        jt808Message.setOriginDataBytes(messageBytes);
        jt808Message.setOriginData(ByteHexUtils.parseHex(messageBytes));
        int offset = 0;
        byte[] jt808MessageId = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        jt808Message.setMessageIdBytes(jt808MessageId);
        jt808Message.setMessageHex(ByteHexUtils.parseHex(jt808MessageId));
        jt808Message.setMessageId(ByteHexUtils.parseTwoInt(jt808MessageId));
        byte[] jt808MessageBodyProps = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        jt808Message.setMessageBodyPropsBytes(jt808MessageBodyProps);
        jt808Message.setMessageBodyProps(ByteHexUtils.parseHex(jt808MessageBodyProps));
        /** 通过消息体属性中的版本标识位 判断是否是 2019版本协议 并增加相关解析 */
        boolean isVersion2019 = Jt808Utils.isVersion2019(jt808MessageBodyProps);
        jt808Message.setVersion2019(isVersion2019);
        byte[] phoneBytes;
        if (isVersion2019) {
            byte[] protocolVersion = new byte[]{messageBytes[offset++]};
            jt808Message.setProtocolVersionBytes(protocolVersion);
            jt808Message.setProtocolVersion(ByteHexUtils.parseHex(protocolVersion));
            phoneBytes = new byte[]{
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++],
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++], messageBytes[offset++]
            };
        } else {
            phoneBytes = new byte[]{
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++],
                    messageBytes[offset++], messageBytes[offset++], messageBytes[offset++]
            };
        }
        jt808Message.setPhoneBytes(phoneBytes);
        jt808Message.setPhone(ByteHexUtils.parseHex(phoneBytes));
        byte[] flowId = new byte[]{messageBytes[offset++], messageBytes[offset++]};
        jt808Message.setFlowIdBytes(flowId);
        jt808Message.setFlowId(ByteHexUtils.parseTwoInt(flowId));
        boolean isSlicePackage = Jt808Utils.isSlicePackage(jt808MessageBodyProps);
        jt808Message.setSlicePackage(isSlicePackage);
        if (isSlicePackage) {
            byte[] packageSize = new byte[]{messageBytes[offset++], messageBytes[offset++]};
            jt808Message.setPackageSizeBytes(packageSize);
            jt808Message.setPackageSize(ByteHexUtils.parseTwoInt(packageSize));
            byte[] packageIndex = new byte[]{messageBytes[offset++], messageBytes[offset]};
            jt808Message.setPackageIndexBytes(packageIndex);
            jt808Message.setPackageIndex(ByteHexUtils.parseTwoInt(packageIndex));
        }
        return jt808Message;
    }
}
