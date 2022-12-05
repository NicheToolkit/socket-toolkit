package io.github.nichetoolkit.socket.configure;

import io.github.nichetoolkit.socket.codec.MessageCoder;
import io.github.nichetoolkit.socket.codec.SocketJt808MessageCoder;
import io.github.nichetoolkit.socket.manager.Jt808MessageHandler;
import io.github.nichetoolkit.socket.server.handler.SocketMessageHandler;
import io.github.nichetoolkit.socket.service.Jt808CacheService;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.service.Jt808DefaultCacheService;
import io.github.nichetoolkit.socket.service.Jt808DefaultDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>SocketJt808AutoConfigure</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
@Configuration
@ComponentScan(value = "io.github.nichetoolkit.socket")
public class SocketJt808AutoConfigure {
    public SocketJt808AutoConfigure() {
        log.debug("================= socket-jt808-auto-configure initiated ！ ===================");
    }

    @Bean
    public MessageCoder messageCoder() {
        return new SocketJt808MessageCoder();
    }

    @Bean
    @ConditionalOnMissingBean(Jt808DataService.class)
    public Jt808DataService defaultJt808DataService(SocketJt808Properties jt808Properties) {
        log.debug("use default jt808 data service bean.");
        return new Jt808DefaultDataService(jt808Properties);
    }

    @Bean
    @ConditionalOnMissingBean(Jt808CacheService.class)
    public Jt808CacheService defaultJt808CacheService(SocketJt808Properties jt808Properties) {
        log.debug("use default jt808 cache service bean.");
        return new Jt808DefaultCacheService(jt808Properties);
    }


    @Bean
    @ConditionalOnMissingBean(SocketMessageHandler.class)
    public SocketMessageHandler jt808MessageHandler() {
        log.debug("use default jt808 message handler bean.");
        return new Jt808MessageHandler();
    }

}
