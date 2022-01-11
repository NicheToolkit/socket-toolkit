package io.github.nichetoolkit.socket.server.mina;

import io.github.nichetoolkit.socket.configure.SocketServerProperties;
import io.github.nichetoolkit.socket.constant.SocketServerConstants;
import io.github.nichetoolkit.socket.server.SocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * <p>DefaultMinaServer</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class DefaultMinaServer implements SocketServer {

    private static AbstractIoAcceptor acceptor;

    private SocketServerProperties properties;
    private ExecutorFilter executorFilter;
    private MinaCodecFactory codecFactory;
    private String name;
    private Integer port;
    private IoHandler handler;

    public DefaultMinaServer(SocketServerProperties properties, ExecutorFilter executorFilter, MinaCodecFactory codecFactory, IoHandler handler) {
        this.properties = properties;
        this.executorFilter = executorFilter;
        this.codecFactory = codecFactory;
        this.name = properties.getName();
        this.port = properties.getPort();
        this.handler = handler;
    }

    @Override
    public synchronized boolean start() {
        if (acceptor != null) {
            return acceptor.isActive();
        }
        String protocol = properties.getProtocol();
        boolean isTCP = SocketServerConstants.TCP.equals(protocol);

        if (isTCP) {
            /* processCount 指的是 core process 数，一般是电脑的 CPU核数 + 1 */
            acceptor = new NioSocketAcceptor(properties.getMinaConfig().getProcessSize());
        } else {
            acceptor = new NioDatagramAcceptor();
        }

        acceptor.getFilterChain().addLast(SocketServerConstants.MINA_EXECUTOR_NAME, executorFilter);

        /* 数据校验 以及 粘包 分包处理 过滤器 */
        acceptor.getFilterChain().addLast(SocketServerConstants.MINA_CODEC_NAME, new ProtocolCodecFilter(codecFactory));
        acceptor.setHandler(handler);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, properties.getMinaConfig().getIdleTime());
        acceptor.getSessionConfig().setReadBufferSize(properties.getMinaConfig().getMaxReadSize());
        if (isTCP) {
            ((DefaultSocketSessionConfig) (acceptor.getSessionConfig())).setTcpNoDelay(properties.getTcpNoDelay());
            ((DefaultSocketSessionConfig) (acceptor.getSessionConfig())).setKeepAlive(properties.getKeepalive());
        }

        try {
            acceptor.bind(new InetSocketAddress(properties.getPort()));
            return true;
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean stop() {
        return false;
    }
}
