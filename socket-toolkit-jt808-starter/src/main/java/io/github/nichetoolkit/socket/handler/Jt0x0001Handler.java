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
 * <p>Jt0x0A00Handler 终端通用应答</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0001)
public class Jt0x0001Handler implements SocketPackageHandler {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0001] 0001 [终端通用应答] terminal answer");
        threadPoolExecutor.execute(() -> {
            /** 对应平台消息流水号 */
            int flowId = ByteHexUtils.parseTwoInt(ByteHexUtils.subbyte(messageBodyBytes,0,2));
            /** 平台消息ID */
            String commandId = ByteHexUtils.parseHex(ByteHexUtils.subbyte(messageBodyBytes,2,4));
            /** 终端对应电话号码 */
            String phone = ByteHexUtils.parseHex(phoneBytes);
            /** 消息ID */
            String msg = ByteHexUtils.parseHex(messageIdBytes);
            /** 一般终端应答都会对应下发指令进行 所以需要找到下发指令那条 并保存到其中 */
            dataService.terminalAnswer(phone, flowId, commandId, msg, messageBodyBytes);
        });
        /** 直接返回处理应答成功 */
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x00);
    }
}
