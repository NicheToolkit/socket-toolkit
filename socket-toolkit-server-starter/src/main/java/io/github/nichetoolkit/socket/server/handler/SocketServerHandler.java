package io.github.nichetoolkit.socket.server.handler;

import io.github.nichetoolkit.rest.RestException;
import io.github.nichetoolkit.rest.util.ContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>SocketServerHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class SocketServerHandler {

    private List<SocketMessageHandler> messageHandlers;

    @Autowired
    public SocketServerHandler() {
        this.messageHandlers = ContextUtils.getBeans(SocketMessageHandler.class);
    }

    public void handle(Object session, Object message) throws RestException {
        for (SocketMessageHandler messageHandler : this.messageHandlers) {
            if (messageHandler.supports(session)) {
                messageHandler.doHandle(session, message);
            }
        }
    }
}
