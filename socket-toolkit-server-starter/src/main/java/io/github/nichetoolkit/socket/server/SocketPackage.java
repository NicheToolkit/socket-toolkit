package io.github.nichetoolkit.socket.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <p>SocketPackage</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SocketPackage {
    int messageId();

}
