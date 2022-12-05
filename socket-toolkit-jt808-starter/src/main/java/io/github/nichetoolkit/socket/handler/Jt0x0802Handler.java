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
 * <p>Jt0x0801Handler 存储多媒体检索应答 存储应答即可</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0802)
public class Jt0x0802Handler implements SocketPackageHandler {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0802] 0802 [存储多媒体检索应答] search stored media data answer");
        /** 保存下发指令对应应答里 需要时取消息体进行分析 */
        threadPoolExecutor.execute(() -> {
            int flowId = ByteHexUtils.parseTwoInt(ByteHexUtils.subbyte(messageBodyBytes, 0, 2));
            String phone = ByteHexUtils.parseHex(phoneBytes);
            dataService.terminalAnswer(phone, flowId, "8802", "0802", messageBodyBytes);
        });
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0);
    }
}
