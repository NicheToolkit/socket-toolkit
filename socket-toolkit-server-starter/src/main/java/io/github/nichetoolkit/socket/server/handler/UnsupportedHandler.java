package io.github.nichetoolkit.socket.server.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>UnsupportedHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
public abstract class UnsupportedHandler implements SocketPackageHandler {

    @Override
    public byte[] handle(byte[] phone, byte[] flowId, byte[] messageId, byte[] messageBody) {
        log.warn("[Unsupported] no support terminal command id");
        return commonAnswer(phone,flowId,messageId);
    }

    public abstract byte[] commonAnswer(byte[] phone, byte[] flowId, byte[] messageId);
}
