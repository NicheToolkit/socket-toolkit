package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.constant.SocketJt808Constants;
import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808CacheService;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Jt0x0100Handler 终端注册 </p>
 * 终端注册是为了建立终端和车辆的关系 注册获取鉴权码鉴权完成后才能传输
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0100)
public class Jt0x0100Handler implements SocketPackageHandler {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808DataService dataService;

    @Autowired
    private Jt808CacheService cacheService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0100] 0100 [终端注册] terminal register");
        /** 判断版本 */
        int version;
        if (phoneBytes.length == SocketJt808Constants.PHONE_2019_LENGTH) {
            version = SocketJt808Constants.VERSION_2019;
        } else {
            if (messageBodyBytes.length > SocketJt808Constants.PHONE_2013_LENGTH) {
                version = SocketJt808Constants.VERSION_2013;
            } else {
                version = SocketJt808Constants.VERSION_2011;
            }
        }
        String phone = ByteHexUtils.parseHex(phoneBytes);
        /** 省域ID, 市县域ID, 制造商ID, 终端型号, 终端ID, 车牌颜色 为0时 表示没有车牌, 车牌号码[车牌颜色为0 时 表示VIN-车辆大架号] */
        byte[] provinceId, cityId, producerId, terminalType, terminalId, licenseColor, license;
        int offset = 0;
        provinceId = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 2);
        cityId = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 2);
        if (version == SocketJt808Constants.VERSION_2019) {
            producerId = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 11);
        } else {
            producerId = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 5);
        }
        if (version == SocketJt808Constants.VERSION_2019) {
            terminalType = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 30);
        } else if (version == SocketJt808Constants.VERSION_2013) {
            terminalType = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 20);
        } else {
            terminalType = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 8);
        }
        if (version == SocketJt808Constants.VERSION_2019) {
            terminalId = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 30);
        } else {
            terminalId = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 7);
        }
        licenseColor = ByteHexUtils.subbyte(messageBodyBytes, offset, offset += 1);
        license = ByteHexUtils.subbyte(messageBodyBytes, offset);

        /** 鉴权码需要在调用终端注册的时候自动生成 7 位数 */
        Future<String> authFuture = threadPoolExecutor.submit(() -> {
            /** 省域ID */
            int province = ByteHexUtils.parseTwoInt(provinceId);
            /** 市县域ID */
            int city = ByteHexUtils.parseTwoInt(cityId);

            /** 制造商ID */
            String manufacturer = Jt808Utils.parseAscII(producerId);
            /** 终端型号 */
            String deviceType = Jt808Utils.parseAscII(terminalType);
            /** 终端ID */
            String deviceId = Jt808Utils.parseAscII(terminalId);
            /** 车牌颜色 直接用 byte 即可 车牌号码[车牌颜色为0 时 表示VIN-车辆大架号]*/
            String registerLicense = Jt808Utils.parseGBK(license);
            /**
             * 需要现在平台添加终端以及车辆才能注册成功 屏蔽无关车辆注册登入
             * 需要在平台验证 终端ID 和 车辆标识 存在注册成功 返回鉴权码 否则注册失败
             *
             * 建立终端与车辆之间的关系
             * 通过省ID、市ID、车牌颜色和车牌号唯一查询出车辆信息
             * 通过设备ID、制造商ID、以及设备类型唯一查询出设备信息
             * 然后通过两者信息链接终端和车辆信息
             */
            return dataService.terminalRegister(phone, province, city, manufacturer,
                    deviceType, deviceId, licenseColor[0], registerLicense);
        });

        /** 鉴权码 */
        String auth;
        try {
            /** 返回值有 0000001 车辆被注册 0000002 无车辆 0000003 终端被注册 0000004 或无终端 者 真正的鉴权码 */
            auth = authFuture.get();
        } catch (Exception e) {
            log.warn(e.getMessage());
            log.warn("鉴权码获取失败！");
            auth = null;
        }

        /** 应答 */
        if (auth == null) {
            /** 数据库查询或者之类的出现了异常 直接回复平台失败应答 */
            return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 1);
        } else if (SocketJt808Constants.TERMINAL_REG_HAS_VEHICLE.equals(auth)) {
            /** 车辆已经注册 */
            return Jt808Utils.buildJt8100(phoneBytes, flowIdBytes, (byte) 1, auth);
        } else if (SocketJt808Constants.TERMINAL_REG_NO_VEHICLE.equals(auth)) {
            /** 不存在的车辆 */
            return Jt808Utils.buildJt8100(phoneBytes, flowIdBytes, (byte) 2, auth);
        } else if (SocketJt808Constants.TERMINAL_REG_HAS_TERMINAL.equals(auth)) {
            /** 终端已经注册 */
            return Jt808Utils.buildJt8100(phoneBytes, flowIdBytes, (byte) 3, auth);
        } else if (SocketJt808Constants.TERMINAL_REG_NO_TERMINAL.equals(auth)) {
            /** 不存在的终端 */
            return Jt808Utils.buildJt8100(phoneBytes, flowIdBytes, (byte) 4, auth);
        } else {
            /** 设置鉴权码 */
            cacheService.setAuth(phone, auth);
            /** 正常 */
            return Jt808Utils.buildJt8100(phoneBytes, flowIdBytes, (byte) 0, auth);
        }
    }
}
