package io.github.nichetoolkit.socket.configure;

import io.github.nichetoolkit.rest.util.GeneralUtils;
import io.github.nichetoolkit.socket.server.ServerManager;
import io.github.nichetoolkit.socket.server.SocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

/**
 * <p>SocketServerStarterConfigure</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "nichetoolkit.socket.server.enabled", havingValue = "true")
public class SocketServerStarterConfigure implements ApplicationListener<ApplicationStartedEvent> {

    private final SocketServerProperties properties;

    @Autowired
    public SocketServerStarterConfigure(SocketServerProperties properties) {
        this.properties = properties;
        log.debug("server properties: {}", this.properties);
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        if (!properties.getEnabled()) {
            log.debug("socket server is disabled!");
            return;
        }
        String serverType = properties.getServerType().name().toLowerCase();
        String serverName = properties.getName();
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        SocketServer server = ServerManager.server(serverName);
        if (GeneralUtils.isEmpty(server) && !applicationContext.containsBean(serverName)) {
            log.error("no {} server instance for name {} has been found !", serverType, serverName);
            return;
        } else if (GeneralUtils.isEmpty(server) && applicationContext.containsBean(serverName)) {
            server = (SocketServer) applicationContext.getBean(serverName);
            log.debug("{} server instance for name {} has been found !", serverType, serverName);
        }
        if (server.start()) {
            log.info("{} server of name {} started on port {}", serverType, serverName, properties.getPort());
        } else {
            log.error("{} server of name {} start failed!", serverType, serverName);
        }
    }
}
