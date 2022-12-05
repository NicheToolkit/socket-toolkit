package io.github.nichetoolkit.socket.server;

import io.github.nichetoolkit.socket.configure.SocketServerStarterConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>EnableSocketServer</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SocketServerStarterConfigure.class})
public @interface EnableSocketServer {
}
