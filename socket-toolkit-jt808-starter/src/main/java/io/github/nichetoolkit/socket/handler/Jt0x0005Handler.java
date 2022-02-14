package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.manager.Jt808SessionManager;
import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808CacheService;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Jt0x0005Handler 终端补传分包请求 [2019 新增]</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0005)
public class Jt0x0005Handler implements SocketPackageHandler {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808CacheService cacheService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0005] 0005 [终端补传分包请求] terminal request patch");
        threadPoolExecutor.execute(() -> {
            String phone = ByteHexUtils.parseHex(phoneBytes);
            Map<Integer, byte[]> sentPackages = cacheService.getSentPackages(phone);
            Object session = Jt808SessionManager.get(phone);
            byte[] idList = ByteHexUtils.subbyte(messageBodyBytes, 4);
            for (int i = 0; i < idList.length; i += 2) {
                int id = ByteHexUtils.parseTwoInt(new byte[]{idList[i], idList[i + 1]});
                byte[] pack = sentPackages.get(id);
                if (pack != null) {
                    if (session instanceof ChannelHandlerContext) {
                        ((ChannelHandlerContext) session).writeAndFlush(pack);
                    } else if (session instanceof IoSession) {
                        ((IoSession) session).write(pack);
                    }

                }
            }
        });
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x00);
    }
}
