package io.github.nichetoolkit.socket.configure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>NoticeWebAutoConfigure</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {"io.github.nichetoolkit.socket"})
public class SocketWebAutoConfigure {
    public SocketWebAutoConfigure() {
        log.debug("================= socket-web-auto-configure initiated ！ ===================");
    }
}
