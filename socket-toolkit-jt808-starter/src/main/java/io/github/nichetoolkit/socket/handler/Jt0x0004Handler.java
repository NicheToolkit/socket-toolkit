package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Jt0x0004Handler 查询服务器时间请求[2019 新增]</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0004)
public class Jt0x0004Handler implements SocketPackageHandler {
    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0004] 0004 [查询服务器时间请求] terminal query server datetime");
        return Jt808Utils.buildJt8004(phoneBytes);
    }
}
