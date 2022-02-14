package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Jt0x0700Handler 电子运单上报</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0701)
public class Jt0x0701Handler implements SocketPackageHandler {
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0700] 0701 [电子运单上报] electronic bill report");
        /** 保存电子运单数据 */
        threadPoolExecutor.execute(()-> {
            String phone = ByteHexUtils.parseHex(phoneBytes);
            dataService.eBill(phone, ByteHexUtils.subbyte(messageBodyBytes,4));
        });
        return Jt808Utils.buildJt8001(phoneBytes,flowIdBytes,messageIdBytes,(byte)0x00);
    }
}
