package io.github.nichetoolkit.socket.server;

import io.github.nichetoolkit.socket.configure.SocketServerAutoConfigure;
import io.github.nichetoolkit.socket.configure.SocketServerProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>EnableNettyServer</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SocketServerAutoConfigure.class, SocketServerProperties.class})
public @interface EnableSocketServer {
}
