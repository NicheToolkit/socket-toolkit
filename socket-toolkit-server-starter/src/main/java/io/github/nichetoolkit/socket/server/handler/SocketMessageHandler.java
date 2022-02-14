package io.github.nichetoolkit.socket.server.handler;

import io.github.nichetoolkit.rest.RestException;
import io.netty.channel.ChannelHandlerContext;
import org.apache.mina.core.session.IoSession;

/**
 * <p>SocketServerHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public interface SocketMessageHandler {

    default boolean supports(Object session) {
        if (session instanceof IoSession || session instanceof ChannelHandlerContext) {
            return true;
        }
        return false;
    }

    void beforeMessage(Object message) throws RestException;

    void doHandle(Object session, Object message) throws RestException;

    void afterSession(Object session) throws RestException;
    
}
