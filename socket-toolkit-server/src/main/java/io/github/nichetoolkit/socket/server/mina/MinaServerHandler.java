package io.github.nichetoolkit.socket.server.mina;

import io.github.nichetoolkit.socket.configure.SocketServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.Arrays;

/**
 * <p>MinaServerHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class MinaServerHandler extends IoHandlerAdapter {

    private SocketServerProperties serverProperties;

    public MinaServerHandler(SocketServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus idleStatus){
        log.debug("id: {}, idle: {}", session.getId(), idleStatus.toString());
        if(session.getIdleCount( idleStatus ) > serverProperties.getMinaConfig().getIdleSize()){
            session.closeNow();
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
        log.debug("id: {}, message: {}", session.getId(), throwable.getMessage());
        log.warn(throwable.getMessage());
        session.closeNow();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        byte[] originData  = (byte[]) message;
        log.debug("id: {}, message: {}", session.getId(), Arrays.toString(originData));
    }
}
