package io.github.nichetoolkit.socket.util;

import io.github.nichetoolkit.socket.constant.SocketJt808Constants;
import io.github.nichetoolkit.socket.model.*;
import io.github.nichetoolkit.socket.model.proccessor.Jt808CanDataItemProcessor;
import io.github.nichetoolkit.socket.model.proccessor.Jt808LocationAttacheProcessor;
import io.github.nichetoolkit.socket.model.proccessor.Jt808TerminalParameterProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>analyzeHelper</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
public class AnalyzeUtils {
    /**
     * 回传终端参数分析
     * @param processor 处理器
     * @param data      终端参数项列表
     */
    public static void analyzeParameter(Jt808TerminalParameterProcessor processor, byte[] data) {
        if (data == null) {
            return;
        }
        int pos = 0;
        while (pos < data.length) {
            byte length = data[pos + 4];

            byte[] id = ByteHexUtils.subbyte(data, pos, pos + 4);
            byte[] dataBuf = ByteHexUtils.subbyte(data, pos + 5, pos + length + 5);

            pos += length + 5;

            processor.process(ByteHexUtils.parseHex(id), dataBuf);
        }
    }

    /**
     * 分析终端属性消息
     * @param data 二进制数据
     * @return 返回分析后的对象
     */
    public static Jt808TerminalProperty analyzeTerminalProperty(byte[] data) {
        Jt808TerminalProperty terminalProperty = Jt808TerminalProperty.builder().build();
        // 终端类型
        byte[] terminalType = ByteHexUtils.subbyte(data, 0, 2);
        terminalProperty.setSupportBus((terminalType[1] & SocketJt808Constants.BIN_0X01) == SocketJt808Constants.BIN_0X01);
        terminalProperty.setSupportDangerVehicle((terminalType[1] & SocketJt808Constants.BIN_0X02) == SocketJt808Constants.BIN_0X02);
        terminalProperty.setSupportFreightVehicle((terminalType[1] & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        terminalProperty.setSupportTaxi((terminalType[1] & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        terminalProperty.setSupportRecording((terminalType[1] & SocketJt808Constants.BIN_0X40) == SocketJt808Constants.BIN_0X40);
        terminalProperty.setSupportExtension((terminalType[1] & SocketJt808Constants.BIN_0X80) == SocketJt808Constants.BIN_0X80);
        // 制造商ID
        byte[] manufacturer = ByteHexUtils.subbyte(data, 2, 7);
        terminalProperty.setManufacturer(ByteHexUtils.parseHex(manufacturer));
        // 终端型号
        byte[] terminalModel = ByteHexUtils.subbyte(data, 7, 27);
        terminalProperty.setTerminalModel(ByteHexUtils.parseHex(terminalModel));
        // 终端ID
        byte[] terminalId = ByteHexUtils.subbyte(data, 27, 34);
        terminalProperty.setTerminalId(Jt808Utils.parseGBK(terminalId));
        // 终端SIM卡ICCID
        byte[] terminalIccid = ByteHexUtils.subbyte(data, 34, 44);
        terminalProperty.setIccid(ByteHexUtils.parseBcds(terminalIccid));
        // 终端硬件版本号长度
        int terminalHardwareVersionLength = data[44];
        // 终端已经按版本号
        byte[] terminalHardwareVersion = ByteHexUtils.subbyte(data, 45, 45 + terminalHardwareVersionLength);
        terminalProperty.setTerminalHardVersion(Jt808Utils.parseGBK(terminalHardwareVersion));
        // 终端软件版本号长度
        int terminalSoftwareVersionLength = data[45 + terminalHardwareVersionLength];
        // 终端固件版本号
        byte[] terminalSoftwareVersion = ByteHexUtils.subbyte(data, 46 + terminalHardwareVersionLength,
                46 + terminalHardwareVersionLength + terminalSoftwareVersionLength);
        terminalProperty.setTerminalSoftVersion(Jt808Utils.parseGBK(terminalSoftwareVersion));
        // GNSS模块属性
        byte gnssModuleProp = data[46 + terminalHardwareVersionLength + terminalSoftwareVersionLength];
        terminalProperty.setSupportGps((gnssModuleProp & SocketJt808Constants.BIN_0X01) == SocketJt808Constants.BIN_0X01);
        terminalProperty.setSupportBeidou((gnssModuleProp & SocketJt808Constants.BIN_0X02) == SocketJt808Constants.BIN_0X02);
        terminalProperty.setSupportGlonass((gnssModuleProp & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        terminalProperty.setSupportGalileo((gnssModuleProp & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        // 通信模块属性
        byte connectModuleProp = data[47 + terminalHardwareVersionLength + terminalSoftwareVersionLength];
        terminalProperty.setSupportGprs((connectModuleProp & SocketJt808Constants.BIN_0X01) == SocketJt808Constants.BIN_0X01);
        terminalProperty.setSupportCdma((connectModuleProp & SocketJt808Constants.BIN_0X02) == SocketJt808Constants.BIN_0X02);
        terminalProperty.setSupportTdscdma((connectModuleProp & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        terminalProperty.setSupportWcdma((connectModuleProp & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        terminalProperty.setSupportCdma2000((connectModuleProp & SocketJt808Constants.BIN_0X10) == SocketJt808Constants.BIN_0X10);
        terminalProperty.setSupportTdlte((connectModuleProp & SocketJt808Constants.BIN_0X20) == SocketJt808Constants.BIN_0X20);
        terminalProperty.setSupportOther((connectModuleProp & SocketJt808Constants.BIN_0X80) == SocketJt808Constants.BIN_0X80);
        return terminalProperty;
    }

    /**
     * 分析报警信息
     * @param alarms 报警信息
     * @return 报警对象
     */
    public static Jt808Alarm analyzeAlarm(byte[] alarms) {
        Jt808Alarm alarm = Jt808Alarm.builder().build();
        alarm.setEmergencyAlarm((alarms[3] & SocketJt808Constants.BIN_0X01) == SocketJt808Constants.BIN_0X01);
        alarm.setOverSpeedAlarm((alarms[3] & SocketJt808Constants.BIN_0X02) == SocketJt808Constants.BIN_0X02);
        alarm.setFatigueDrivingAlarm((alarms[3] & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        alarm.setDangerWarning((alarms[3] & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        alarm.setGnssModuleFault((alarms[3] & SocketJt808Constants.BIN_0X10) == SocketJt808Constants.BIN_0X10);
        alarm.setGnssConnectFault((alarms[3] & SocketJt808Constants.BIN_0X20) == SocketJt808Constants.BIN_0X20);
        alarm.setGnssShortCircuit((alarms[3] & SocketJt808Constants.BIN_0X40) == SocketJt808Constants.BIN_0X40);
        alarm.setPowerUnderpressure((alarms[3] & SocketJt808Constants.BIN_0X80) == SocketJt808Constants.BIN_0X80);
        alarm.setPowerFault((alarms[2] & SocketJt808Constants.BIN_0X01) == SocketJt808Constants.BIN_0X01);
        alarm.setLcdFault((alarms[2] & SocketJt808Constants.BIN_0X02) == SocketJt808Constants.BIN_0X02);
        alarm.setTtsFault((alarms[2] & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        alarm.setCameraFault((alarms[2] & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        alarm.setIcModuleFault((alarms[2] & SocketJt808Constants.BIN_0X10) == SocketJt808Constants.BIN_0X10);
        alarm.setOverSpeedWarn((alarms[2] & SocketJt808Constants.BIN_0X20) == SocketJt808Constants.BIN_0X20);
        alarm.setFatigueDrivingWarn((alarms[2] & SocketJt808Constants.BIN_0X40) == SocketJt808Constants.BIN_0X40);
        alarm.setDriverAgainstRules((alarms[2] & SocketJt808Constants.BIN_0X80) == SocketJt808Constants.BIN_0X80);
        alarm.setTirePressureWarning((alarms[1] & SocketJt808Constants.BIN_0X01) == SocketJt808Constants.BIN_0X01);
        alarm.setRightTurnBlindArea((alarms[1] & SocketJt808Constants.BIN_0X02) == SocketJt808Constants.BIN_0X02);
        alarm.setCumulativeDrivingTimeout((alarms[1] & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        alarm.setStopTimeout((alarms[1] & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        alarm.setInArea((alarms[1] & SocketJt808Constants.BIN_0X10) == SocketJt808Constants.BIN_0X10);
        alarm.setOutLine((alarms[1] & SocketJt808Constants.BIN_0X20) == SocketJt808Constants.BIN_0X20);
        alarm.setDrivingTimeIncorrect((alarms[1] & SocketJt808Constants.BIN_0X40) == SocketJt808Constants.BIN_0X40);
        alarm.setRouteDeviation((alarms[1] & SocketJt808Constants.BIN_0X80) == SocketJt808Constants.BIN_0X80);
        alarm.setVssFault((alarms[0] & SocketJt808Constants.BIN_0X01) == SocketJt808Constants.BIN_0X01);
        alarm.setOilFault((alarms[0] & SocketJt808Constants.BIN_0X02) == SocketJt808Constants.BIN_0X02);
        alarm.setStolenVehicle((alarms[0] & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        alarm.setIllegalIgnition((alarms[0] & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        alarm.setIllegalDisplacement((alarms[0] & SocketJt808Constants.BIN_0X10) == SocketJt808Constants.BIN_0X10);
        alarm.setCollisionWarn((alarms[0] & SocketJt808Constants.BIN_0X20) == SocketJt808Constants.BIN_0X20);
        alarm.setRollOverWarn((alarms[0] & SocketJt808Constants.BIN_0X40) == SocketJt808Constants.BIN_0X40);
        alarm.setIllegalOpeningTheDoor((alarms[0] & SocketJt808Constants.BIN_0X80) == SocketJt808Constants.BIN_0X80);
        return alarm;
    }

    /**
     * 分析状态信息
     * @param statusBytes 状态信息
     * @return 状态对象
     */
    public static Jt808Status analyzeStatus(byte[] statusBytes) {
        Jt808Status status = Jt808Status.builder().build();
        status.setAcc((statusBytes[3] & SocketJt808Constants.BIN_0X01) == SocketJt808Constants.BIN_0X01);
        status.setPositioning((statusBytes[3] & SocketJt808Constants.BIN_0X02) == SocketJt808Constants.BIN_0X02);
        status.setSouth((statusBytes[3] & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        status.setWest((statusBytes[3] & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        status.setSuspended((statusBytes[3] & SocketJt808Constants.BIN_0X10) == SocketJt808Constants.BIN_0X10);
        status.setEncryption((statusBytes[3] & SocketJt808Constants.BIN_0X20) == SocketJt808Constants.BIN_0X20);
        status.setBrakeSystemWarning((statusBytes[3] & SocketJt808Constants.BIN_0X40) == SocketJt808Constants.BIN_0X40);
        status.setLaneDepartureWarning((statusBytes[3] & SocketJt808Constants.BIN_0X80) == SocketJt808Constants.BIN_0X80);
        status.setCargo(statusBytes[2] & 0x03);
        status.setOilBreak((statusBytes[2] & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        status.setCircuitBreak((statusBytes[2] & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        status.setLocking((statusBytes[2] & SocketJt808Constants.BIN_0X10) == SocketJt808Constants.BIN_0X10);
        status.setOpening1((statusBytes[2] & SocketJt808Constants.BIN_0X20) == SocketJt808Constants.BIN_0X20);
        status.setOpening2((statusBytes[2] & SocketJt808Constants.BIN_0X40) == SocketJt808Constants.BIN_0X40);
        status.setOpening3((statusBytes[2] & SocketJt808Constants.BIN_0X80) == SocketJt808Constants.BIN_0X80);
        status.setOpening4((statusBytes[1] & SocketJt808Constants.BIN_0X01) == SocketJt808Constants.BIN_0X01);
        status.setOpening5((statusBytes[1] & SocketJt808Constants.BIN_0X02) == SocketJt808Constants.BIN_0X02);
        status.setGps((statusBytes[1] & SocketJt808Constants.BIN_0X04) == SocketJt808Constants.BIN_0X04);
        status.setBeidou((statusBytes[1] & SocketJt808Constants.BIN_0X08) == SocketJt808Constants.BIN_0X08);
        status.setGlonass((statusBytes[1] & SocketJt808Constants.BIN_0X10) == SocketJt808Constants.BIN_0X10);
        status.setGalileo((statusBytes[1] & SocketJt808Constants.BIN_0X20) == SocketJt808Constants.BIN_0X20);
        status.setVehicleStatus((statusBytes[1] & SocketJt808Constants.BIN_0X40) == SocketJt808Constants.BIN_0X40);
        // 23 - 31 保留
        return status;
    }

    /**
     * 定位附属信息
     * @param processor 处理器
     * @param attache   附属信息
     */
    public static void analyzeAttache(Jt808LocationAttacheProcessor processor, byte[] attache) {
        if (attache == null) {
            return;
        }
        int pos = 0;
        while (pos < attache.length) {
            byte length = attache[pos + 1];

            int id = attache[pos];
            byte[] dataBuf = ByteHexUtils.subbyte(attache, pos + 2, pos + length + 2);

            pos += length + 2;

            processor.process(id, dataBuf);
        }
    }

    /**
     * 分析定位信息
     * @param msgBody 定位消息体
     * @return 定位信息
     */
    public static Jt808Location analyzeLocation(byte[] msgBody) {
        // 报警标识
        byte[] alarms = ByteHexUtils.subbyte(msgBody, 0, 4);
        // 状态
        byte[] statuses = ByteHexUtils.subbyte(msgBody, 4, 8);
        // 纬度[以度为单位的值乘以 10 的 6 次方，精确到百万分之一度]
        byte[] latitude = ByteHexUtils.subbyte(msgBody, 8, 12);
        // 经度[以度为单位的值乘以 10 的 6 次方，精确到百万分之一度]
        byte[] longitude = ByteHexUtils.subbyte(msgBody, 12, 16);
        // 高程 [单位 m]
        byte[] height = ByteHexUtils.subbyte(msgBody, 16, 18);
        // 速度 [单位 0.1 km/h]
        byte[] speed = ByteHexUtils.subbyte(msgBody, 18, 20);
        // 方向 [0~359 正北为0 顺时针]
        byte[] direction = ByteHexUtils.subbyte(msgBody, 20, 22);
        // 时间 [yy-mm-dd-hh-mm-ss]
        byte[] datetime = ByteHexUtils.subbyte(msgBody, 22, 28);
        // 附加
        byte[] attache = ByteHexUtils.subbyte(msgBody, 28);

        Jt808Alarm alarm = analyzeAlarm(alarms);
        Jt808Status status = analyzeStatus(statuses);
        double longitudeDouble = (double) ByteHexUtils.parseFourInt(longitude) / (double) 1000000;
        double latitudeDouble = (double) ByteHexUtils.parseFourInt(latitude) / (double) 1000000;
        int heightInt = ByteHexUtils.parseTwoInt(height);
        double speedDouble = (double) (ByteHexUtils.parseTwoInt(speed)) / (double) 10;
        int directionInt = ByteHexUtils.parseTwoInt(direction);
        String datetimeString = Jt808Utils.parseDatetime(datetime);
        List<Jt808LocationAttach> attachList = new ArrayList<>();
        analyzeAttache((id, data) -> {
            Jt808LocationAttach attach = Jt808LocationAttach.builder().build();
            attach.setId(id);
            attach.setData(data);
            attachList.add(attach);
        }, attache);


        Jt808Location location = Jt808Location.builder().build();
        location.setAlarm(alarm);
        location.setStatus(status);
        location.setLongitude(longitudeDouble);
        location.setLatitude(latitudeDouble);
        location.setHeight(heightInt);
        location.setSpeed(speedDouble);
        location.setDirection(directionInt);
        location.setDatetime(datetimeString);
        location.setAttaches(attachList);
        return location;
    }

    /**
     * 分析驾驶员上报信息 兼容 2011 版本协议、 2013 版本协议和2019版本协议
     * @param phoneNum 电话
     * @param data     上报数据
     * @return 驾驶员信息
     */
    public static Jt808Driver analyzeDriver(byte[] phoneNum, byte[] data) {
        // 版本信息
        int ver = 0;

        if (phoneNum.length == SocketJt808Constants.NUMBER_10) {
            ver = SocketJt808Constants.YEAR_2019;
        } else {
            // 拔卡 没有信息 只有7字节消息
            if (data[SocketJt808Constants.NUMBER_0] == SocketJt808Constants.NUMBER_2 && data.length == SocketJt808Constants.NUMBER_7) {
                ver = SocketJt808Constants.YEAR_2013;
            } else if (data[SocketJt808Constants.NUMBER_0] == SocketJt808Constants.NUMBER_1 && data.length >= SocketJt808Constants.NUMBER_8) {
                if (data[SocketJt808Constants.NUMBER_7] == SocketJt808Constants.NUMBER_0) {
                    // 这里需要通过解析消息长度判断版本
                    int len2011 = 0, len2013 = 0;
                    try {
                        len2011 = 62 + data[0] + data[data[0] + 61];
                    } catch (IndexOutOfBoundsException e) {
                        log.warn(e.getMessage());
                    }
                    try {
                        len2013 = 34 + data[8] + data[data[8] + 29];
                    } catch (IndexOutOfBoundsException e) {
                        log.warn(e.getMessage());
                    }

                    if (data.length == len2011) {
                        ver = SocketJt808Constants.YEAR_2011;
                    }
                    if (data.length == len2013) {
                        ver = SocketJt808Constants.YEAR_2013;
                    }
                } else if (data[SocketJt808Constants.NUMBER_7] == SocketJt808Constants.NUMBER_1
                        || data[SocketJt808Constants.NUMBER_7] == SocketJt808Constants.NUMBER_2
                        || data[SocketJt808Constants.NUMBER_7] == SocketJt808Constants.NUMBER_3
                        || data[SocketJt808Constants.NUMBER_7] == SocketJt808Constants.NUMBER_4) {
                    if (data.length == SocketJt808Constants.NUMBER_8) {
                        ver = SocketJt808Constants.YEAR_2013;
                    } else {
                        ver = SocketJt808Constants.YEAR_2011;
                    }
                } else {
                    ver = SocketJt808Constants.YEAR_2011;
                }
            } else {
                ver = SocketJt808Constants.YEAR_2011;
            }
        }


        if (ver == SocketJt808Constants.YEAR_2011) {
            return analyzeDriver2011(data);
        } else if (ver == SocketJt808Constants.YEAR_2013) {
            return analyzeDriver2013(data);
        } else if (ver == SocketJt808Constants.YEAR_2019) {
            return analyzeDriver2019(data);
        }
        return null;
    }

    private static Jt808Driver analyzeDriver2011(byte[] data) {
        // 姓名长度
        int nameLength = data[0];
        // 驾驶员姓名
        byte[] name = ByteHexUtils.subbyte(data, 1, nameLength + 1);
        // 身份证编码
        byte[] idCard = ByteHexUtils.subbyte(data, nameLength + 1, nameLength + 21);
        // 从业资格证编码
        byte[] certificate = ByteHexUtils.subbyte(data, nameLength + 21, nameLength + 61);
        // 从业资格证发证机构名称长度 最后全是 所以不需要
//        int certificatePublishAgentNameLength = data[nameLength + 61];
        // 从业资格证发证机构名称
        byte[] certificatePublishAgentName = ByteHexUtils.subbyte(data, nameLength + 62);

        Jt808Driver driver = Jt808Driver.builder().build();

        driver.setDriverName(Jt808Utils.parseGBK(name));
        driver.setIdCardNumber(Jt808Utils.parseGBK(idCard));
        driver.setCertificateNumber(Jt808Utils.parseGBK(certificate));
        driver.setCertificatePublishAgentName(Jt808Utils.parseGBK(certificatePublishAgentName));

        driver.setSuccess(true);

        return driver;
    }

    private static Jt808Driver analyzeDriver2013(byte[] data) {
        Jt808Driver driver = Jt808Driver.builder().build();
        driver.setDriverAlarm(Jt808DriverAlarm.builder().build());

        if (data[0] == SocketJt808Constants.NUMBER_1) {
            // 驾驶员上班 卡插入
            driver.getDriverAlarm().setPullOutCard(false);
        } else if (data[0] == SocketJt808Constants.NUMBER_2) {
            // 驾驶员下班 卡拔出
            driver.getDriverAlarm().setPullOutCard(true);
        } else {
            // 2013 第一个字节 不支持其他形式
            return null;
        }
        String gTime = Jt808Utils.parseDatetime(ByteHexUtils.subbyte(data, 1, 7));

        driver.setDatetime(gTime);

        // 拔卡则不再继续解析
        if (!driver.getDriverAlarm().isPullOutCard()) {
            switch (data[7]) {
                case 0x00:
                    // IC 卡读卡成功 读取驾驶员信息
                    int nameLength = data[8];
                    String driverName = Jt808Utils.parseGBK(ByteHexUtils.subbyte(data, 9,
                            nameLength + 9));
                    String certificate = Jt808Utils.parseGBK(ByteHexUtils.subbyte(data, nameLength + 9,
                            nameLength + 29));
                    int certificatePublishAgentNameLength = data[nameLength + 29];
                    String certificatePublishAgentName =
                            Jt808Utils.parseGBK(ByteHexUtils.subbyte(data, nameLength + 30,
                                    nameLength + 30 + certificatePublishAgentNameLength));
                    String expiryTime = ByteHexUtils.parseBcds(ByteHexUtils.subbyte(data,
                            nameLength + 30 + certificatePublishAgentNameLength));

                    driver.setDriverName(driverName);
                    driver.setCertificateNumber(certificate);
                    driver.setCertificatePublishAgentName(certificatePublishAgentName);
                    driver.setCertificateLimitDate(expiryTime);

                    break;
                case 0x01:
                    // 读卡失败，原因为卡片密钥认证未通过
                    driver.getDriverAlarm().setUnAuthentication(true);
                    break;
                case 0x02:
                    // 读卡失败，原因为卡片已被锁定
                    driver.getDriverAlarm().setLocked(true);
                    break;
                case 0x03:
                    // 读卡失败，原因为卡片被拔出
                    driver.getDriverAlarm().setPullOut(true);
                    break;
                case 0x04:
                    // 读卡失败，原因为数据校验错误
                    driver.getDriverAlarm().setCheckFailed(true);
                    break;
                default:
                    return null;
            }
        }

        driver.setSuccess(true);

        return driver;
    }

    private static Jt808Driver analyzeDriver2019(byte[] data) {
        Jt808Driver driver = Jt808Driver.builder().build();
        driver.setDriverAlarm(Jt808DriverAlarm.builder().build());

        if (data[0] == SocketJt808Constants.NUMBER_1) {
            // 驾驶员上班 卡插入
            driver.getDriverAlarm().setPullOutCard(false);
        } else if (data[0] == SocketJt808Constants.NUMBER_2) {
            // 驾驶员下班 卡拔出 没有其他信息
            driver.getDriverAlarm().setPullOutCard(true);
        } else {
            // 2019 第一个字节 不支持其他形式
            return null;
        }
        String gTime = Jt808Utils.parseDatetime(ByteHexUtils.subbyte(data, 1, 7));

        driver.setDatetime(gTime);

        // 拔卡则不再继续解析
        if (!driver.getDriverAlarm().isPullOutCard()) {
            switch (data[7]) {
                case 0x00:
                    // IC 卡读卡成功 读取驾驶员信息
                    int nameLength = data[8];
                    String driverName = Jt808Utils.parseGBK(ByteHexUtils.subbyte(data, 9,
                            nameLength + 9));
                    String certificate = Jt808Utils.parseGBK(ByteHexUtils.subbyte(data, nameLength + 9,
                            nameLength + 29));
                    int certificatePublishAgentNameLength = data[nameLength + 29];
                    String certificatePublishAgentName =
                            Jt808Utils.parseGBK(ByteHexUtils.subbyte(data, nameLength + 30,
                                    nameLength + 30 + certificatePublishAgentNameLength));
                    String expiryTime = ByteHexUtils.parseBcds(ByteHexUtils.subbyte(data,
                            nameLength + certificatePublishAgentNameLength + 30,
                            nameLength + certificatePublishAgentNameLength + 34));
                    String idCardNum = Jt808Utils.parseGBK(ByteHexUtils.subbyte(
                            data,
                            nameLength + certificatePublishAgentNameLength + 34,
                            nameLength + certificatePublishAgentNameLength + 54
                    ));

                    driver.setDriverName(driverName);
                    driver.setCertificateNumber(certificate);
                    driver.setCertificatePublishAgentName(certificatePublishAgentName);
                    driver.setCertificateLimitDate(expiryTime);
                    driver.setIdCardNumber(idCardNum);

                    break;
                case 0x01:
                    // 读卡失败，原因为卡片密钥认证未通过
                    driver.getDriverAlarm().setUnAuthentication(true);
                    break;
                case 0x02:
                    // 读卡失败，原因为卡片已被锁定
                    driver.getDriverAlarm().setLocked(true);
                    break;
                case 0x03:
                    // 读卡失败，原因为卡片被拔出
                    driver.getDriverAlarm().setPullOut(true);
                    break;
                case 0x04:
                    // 读卡失败，原因为数据校验错误
                    driver.getDriverAlarm().setCheckFailed(true);
                    break;
                default:
                    return null;
            }
        }

        driver.setSuccess(true);

        return driver;
    }

    /**
     * 分析CAN总线上传数据
     * @param data 相关消息体
     */
    public static Jt808CanData analyzeCan(byte[] data) {

        Jt808CanData canData = Jt808CanData.builder().build();

        canData.setTimestamp(System.currentTimeMillis());

        String time = ByteHexUtils.parseBcd(new byte[]{data[2]}) + "-" +
                ByteHexUtils.parseBcd(new byte[]{data[3]}) + "-" +
                ByteHexUtils.parseBcd(new byte[]{data[4]}) + "-" +
                ByteHexUtils.parseBcd(new byte[]{data[5]}) +
                ByteHexUtils.parseBcd(new byte[]{data[6]});

        canData.setReceiveTime(time);
        canData.setData(ByteHexUtils.subbyte(data, 7));
        return canData;
    }

    /**
     * 分析CAN总线数据项
     * @param data 参总线项目列表
     */
    public static void analyzeCanItem(Jt808CanDataItemProcessor processor, byte[] data) {
        for (int pos = 0, len = 12; pos < data.length; pos += len) {
            byte[] head = ByteHexUtils.subbyte(data, pos, pos + 4);
            byte[] tail = ByteHexUtils.subbyte(data, pos + 4, pos + len);
            Jt808CanDataItem item = Jt808CanDataItem.builder().build();

            int headInt = ByteHexUtils.parseFourInt(head);

            item.setCanTunnel(headInt & 0x80000000 >> 31);
            item.setFrameType(headInt & 0x40000000 >> 30);
            item.setDataCollectModel(headInt & 0x20000000 >> 29);
            item.setCanID(headInt & 0x1fffffff);
            item.setData(tail);

            processor.process(item);
        }
    }

    /**
     * 分析多媒体数据信息
     * @param data 多媒体信息
     * @return 分析结果
     */
    public static Jt808Media analyzeMedia(byte[] data) {
        Jt808Media media = Jt808Media.builder().build();
        media.setMediaId(ByteHexUtils.parseFourInt(ByteHexUtils.subbyte(data, 0, 4)));
        media.setMediaType(data[4]);
        media.setMediaFormat(data[5]);
        media.setEventNumber(data[6]);
        media.setTunnelId(data[7]);
        return media;
    }

    /**
     * 数据透传分析
     * @param data 消息体
     * @return 分析结果
     */
    public static Jt808DataTransport analyzeDataTransport(byte[] data) {
        Jt808DataTransport dataTransport = Jt808DataTransport.builder().build();
        dataTransport.setType(data[0]);
        dataTransport.setData(ByteHexUtils.subbyte(data, 1));
        return dataTransport;
    }
}
