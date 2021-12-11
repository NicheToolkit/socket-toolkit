package io.github.nichetoolkit.socket.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>ServerManager</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class ServerManager {
    private final static Map<String, SocketServer> SERVER_MAP = new ConcurrentHashMap<>();

    public static SocketServer server(String name) {
        return SERVER_MAP.get(name);
    }

    public static void add(String name, SocketServer server) {
        SERVER_MAP.putIfAbsent(name, server);
    }
}
