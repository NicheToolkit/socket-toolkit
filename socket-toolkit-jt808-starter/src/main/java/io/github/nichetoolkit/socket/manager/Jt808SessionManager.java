package io.github.nichetoolkit.socket.manager;

import io.github.nichetoolkit.rest.util.GeneralUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.mina.core.session.IoSession;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>SessionManager</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class Jt808SessionManager {

    public static SoftReference<Map<String, Map<Integer, byte[]>>> PACKAGE_MAP_REFERENCE = new SoftReference<>(new HashMap<>());

    public static Map<String, String> AUTH_MAP = new ConcurrentHashMap<>();

    public static Map<String, Map<Integer, byte[]>> SENT_PACKAGE_MAP = new ConcurrentHashMap<>();

    private final static Map<String, Object> SESSION_MAP = new ConcurrentHashMap<>();

    public static boolean contains(String phone) {
        return SESSION_MAP.containsKey(phone);
    }

    public static Object get(String phone) {
        return SESSION_MAP.get(phone);
    }

    public static void set(String phone, Object session) {
        SESSION_MAP.put(phone, session);
    }

    public static void write(String phone, byte[] data) {
        Object session = get(phone);
        if (GeneralUtils.isNotEmpty(phone)) {
            if (session instanceof ChannelHandlerContext) {
                ((ChannelHandlerContext) session).writeAndFlush(data);
            } else if (session instanceof IoSession) {
                ((IoSession) session).write(data);
            }
        }
    }
}
