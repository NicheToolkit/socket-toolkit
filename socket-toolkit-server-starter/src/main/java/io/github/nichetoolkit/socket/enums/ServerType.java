package io.github.nichetoolkit.socket.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.nichetoolkit.rest.RestValue;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * <p>ServerType</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public enum ServerType implements RestValue<String,String> {
    NETTY("netty","netty-server"),
    MINA("mina","mina-server")
    ;

    private final String key;
    private final String value;

    ServerType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @JsonValue
    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static ServerType parserKey(@NonNull String key) {
        ServerType typeEnum = RestValue.parseKey(ServerType.class, key);
        return Optional.ofNullable(typeEnum).orElse(ServerType.NETTY);
    }

    public static ServerType parserValue(@NonNull String value) {
        ServerType typeEnum = RestValue.parseValue(ServerType.class, value);
        return Optional.ofNullable(typeEnum).orElse(ServerType.NETTY);
    }

}
