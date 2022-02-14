package io.github.nichetoolkit.socket.service;

import io.github.nichetoolkit.socket.configure.SocketJt808Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Jt808DefaultService</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class Jt808DefaultCacheService implements Jt808CacheService {

    private SocketJt808Properties jt808Properties;

    @Autowired
    public Jt808DefaultCacheService(SocketJt808Properties jt808Properties) {
        this.jt808Properties = jt808Properties;
    }
}
