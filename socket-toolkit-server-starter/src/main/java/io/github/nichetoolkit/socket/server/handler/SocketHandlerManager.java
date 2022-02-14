package io.github.nichetoolkit.socket.server.handler;

import io.github.nichetoolkit.rest.holder.ContextHolder;
import io.github.nichetoolkit.rest.util.ContextUtils;
import io.github.nichetoolkit.rest.util.GeneralUtils;
import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>SocketHandlerManager</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class SocketHandlerManager {

    private static UnsupportedHandler UNSUPPORTED_HANDLER;

    private static final Map<Integer, SocketPackageHandler> PACKAGE_HANDLER_MAP = new ConcurrentHashMap<>(30);

    public static Map<Integer, SocketPackageHandler> packageHandlerMap() {
        return PACKAGE_HANDLER_MAP;
    }

    public static SocketPackageHandler handler(int messageId) {
        SocketPackageHandler socketPackageHandler = PACKAGE_HANDLER_MAP.get(messageId);
        return socketPackageHandler == null ? UNSUPPORTED_HANDLER : socketPackageHandler;
    }

    @Autowired
    public SocketHandlerManager() {
        initSocketPackageHandler();
    }

    public static void initSocketPackageHandler() {
        UNSUPPORTED_HANDLER = ContextUtils.getBean(UnsupportedHandler.class);
        Map<String, Object> socketPackageHandlerMap =  ContextHolder.getApplicationContext().getBeansWithAnnotation(SocketPackage.class);
        if (GeneralUtils.isNotEmpty(socketPackageHandlerMap)) {
            for (Map.Entry<String, Object> entry : socketPackageHandlerMap.entrySet()) {
                Object handler = entry.getValue();
                if (handler instanceof SocketPackageHandler) {
                    SocketPackage socketPackage = handler.getClass().getAnnotation(SocketPackage.class);
                    if (GeneralUtils.isNotEmpty(socketPackage)) {
                        int messageId = socketPackage.messageId();
                        SocketPackageHandler socketPackageHandler = PACKAGE_HANDLER_MAP.get(messageId);
                        if (GeneralUtils.isEmpty(socketPackageHandler)) {
                            PACKAGE_HANDLER_MAP.put(messageId,(SocketPackageHandler)handler);
                            log.debug("add package handler {} for {}", handler.getClass().getName(),
                                    ByteHexUtils.parseHex(ByteHexUtils.parseTwoByte(messageId)));
                        }
                    }
                }

            }
        }
    }


}
