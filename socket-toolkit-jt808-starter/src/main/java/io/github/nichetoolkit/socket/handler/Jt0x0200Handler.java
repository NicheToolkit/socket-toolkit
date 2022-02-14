package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.model.Jt808Location;
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
 * <p>Jt0x0200Handler 终端位置信息汇报</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0200)
public class Jt0x0200Handler implements SocketPackageHandler {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0200] 0200 [终端位置信息汇报] terminal location info report");
        threadPoolExecutor.execute(() -> {
            String phone = ByteHexUtils.parseHex(phoneBytes);
            Jt808Location location = AnalyzeUtils.analyzeLocation(messageBodyBytes);
            dataService.terminalLocation(phone, location, null);
        });
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0);
    }
}
