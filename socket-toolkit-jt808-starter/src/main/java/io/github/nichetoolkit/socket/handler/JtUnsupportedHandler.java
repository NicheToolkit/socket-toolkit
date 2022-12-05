package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.server.handler.UnsupportedHandler;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>JtUnsupportedHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@Component
public class JtUnsupportedHandler extends UnsupportedHandler {

    @Override
    public byte[] commonAnswer(byte[] phone, byte[] flowId, byte[] messageId) {
        return Jt808Utils.buildJt8001(phone,flowId,messageId,(byte) 3);
    }
}
