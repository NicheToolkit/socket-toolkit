package io.github.nichetoolkit.socket.server.mina;

import io.github.nichetoolkit.socket.configure.SocketServerProperties;
import io.github.nichetoolkit.socket.server.handler.SocketMessageHandler;
import io.github.nichetoolkit.socket.server.handler.SocketServerHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;

import java.util.Arrays;

/**
 * <p>MinaServerHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class MinaServerHandler extends IoHandlerAdapter {

    private SocketServerHandler socketServerHandler;

    private SocketServerProperties serverProperties;

    public MinaServerHandler(SocketServerHandler socketServerHandler, SocketServerProperties serverProperties) {
        this.socketServerHandler = socketServerHandler;
        this.serverProperties = serverProperties;
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus idleStatus){
        log.debug("session idle, id: {} ", session.getId());
        if(session.getIdleCount( idleStatus ) > serverProperties.getMinaConfig().getIdleSize()){
            session.closeNow();
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        log.debug("session created, id: {} ", session.getId());
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        log.debug("session opened, id: {} ", session.getId());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        log.debug("session closed, id: {} ", session.getId());
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        log.debug("session sent message, id: {} ", session.getId());

    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        super.inputClosed(session);
        log.debug("session input closed, id: {} ", session.getId());
    }

    @Override
    public void event(IoSession session, FilterEvent event) throws Exception {
        super.event(session, event);
        log.debug("session user event, id: {} ", session.getId());
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
        log.error("id: {}, message: {}", session.getId(), throwable.getMessage());
        session.closeNow();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        byte[] originData  = (byte[]) message;
        log.debug("id: {}, message: {}", session.getId(), Arrays.toString(originData));
        socketServerHandler.handle(session,message);
    }
}
