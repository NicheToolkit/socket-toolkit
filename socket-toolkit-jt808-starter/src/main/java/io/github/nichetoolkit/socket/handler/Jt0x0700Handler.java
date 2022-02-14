package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Jt0x0700Handler 行驶记录上传</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0700)
public class Jt0x0700Handler implements SocketPackageHandler {
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0700] 0700 [行驶记录上传] drive data upload");
        String phone = ByteHexUtils.parseHex(phoneBytes);
        int flowId = ByteHexUtils.parseTwoInt(ByteHexUtils.subbyte(messageBodyBytes, 0, 2));
        threadPoolExecutor.execute(() -> dataService.terminalAnswer(phone, flowId, "8700", "0700", messageBodyBytes));
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x00);
    }
}
