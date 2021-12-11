package io.github.nichetoolkit.socket.configure;

import io.github.nichetoolkit.rest.util.common.GeneralUtils;
import io.github.nichetoolkit.socket.constant.SocketServerConstants;
import io.github.nichetoolkit.socket.server.SocketServer;
import io.github.nichetoolkit.socket.server.NettyThreadFactory;
import io.github.nichetoolkit.socket.server.ServerManager;
import io.github.nichetoolkit.socket.server.DefaultNettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>SocketServerAutoConfigure</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
@Configuration
@ComponentScan(value = "io.github.nichetoolkit.socket")
@ConditionalOnProperty(value = "nichetoolkit.socket.server.enabled", havingValue = "true")
public class SocketServerAutoConfigure implements ApplicationListener<ApplicationStartedEvent> {
    private final SocketServerProperties properties;

    @Autowired
    public SocketServerAutoConfigure(SocketServerProperties properties) {
        log.debug("================= socket-server-auto-configure initiated ！ ===================");
        this.properties = properties;
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                properties.getThread().getCorePoolSize(),
                properties.getThread().getMaxPoolSize(),
                properties.getThread().getKeepaliveTime(),
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new NettyThreadFactory(SocketServerConstants.THREAD_PREFIX)
        );
    }

    @Bean
    @ConditionalOnMissingBean(SocketServer.class)
    public SocketServer socketNettyServer() {
        DefaultNettyServer socketNettyServer = new DefaultNettyServer(properties);
        ServerManager.add(properties.getName(),socketNettyServer);
        return socketNettyServer;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        String serverName = properties.getName();
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        SocketServer server = ServerManager.server(serverName);
        if (GeneralUtils.isEmpty(server) && !applicationContext.containsBean(serverName)) {
            log.error("no netty server instance for name {} has been found !", serverName);
            return;
        } else if (GeneralUtils.isEmpty(server) && applicationContext.containsBean(serverName)) {
            server = (SocketServer) applicationContext.getBean(serverName);
            log.debug("netty server instance for name {} has been found !", serverName);
        }
        if (server.start()) {
            log.info("netty server started on port {}", properties.getPort());
        } else {
            log.error("netty server start failed!");
        }
    }
}
