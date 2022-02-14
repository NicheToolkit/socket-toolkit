package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.model.Jt808Driver;
import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.AnalyzeUtils;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Jt0x0700Handler 驾驶员身份信息采集上报</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0702)
public class Jt0x0702Handler implements SocketPackageHandler {
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0702] 0702 [驾驶员身份信息采集上报] driver data collection report");
        String phone = ByteHexUtils.parseHex(phoneBytes);
        Jt808Driver driver = AnalyzeUtils.analyzeDriver(phoneBytes, messageBodyBytes);
        /** 如果等于空 则解析错误 直接返回失败应答 */
        if (driver == null) {
            log.error("{} data analyze failed!", phone);
            return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x01);
        }
        threadPoolExecutor.execute(() -> dataService.driverInfo(phone, driver));

        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x00);
    }
}
