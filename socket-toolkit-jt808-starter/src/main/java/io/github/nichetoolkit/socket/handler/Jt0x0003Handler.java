package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808CacheService;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Jt0x0003Handler 终端注销 取消车辆与终端的关系</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0003)
public class Jt0x0003Handler implements SocketPackageHandler {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808CacheService cacheService;

    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0003] 0003 [终端注销] terminal logout");
        threadPoolExecutor.execute(()->{
            /** 获取终端手机号码 12 位电话号码 */
            String phone  = ByteHexUtils.parseHex(phoneBytes);
            /** 数据库直接删除终端与车辆的关联 */
            dataService.terminalCancel(phone);
            /** 直接删除终端之前的鉴权数据 */
            cacheService.removeAuth(phone);
        });
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x00);
    }
}
