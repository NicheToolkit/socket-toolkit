package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.rest.util.ZipUtils;
import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Jt0x0900Handler 数据压缩上报</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0901)
public class Jt0x0901Handler implements SocketPackageHandler {
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0901] 0901 [数据上行透传] data press upload");
        threadPoolExecutor.execute(() -> {
            String phone = ByteHexUtils.parseHex(phoneBytes);
            /** 因为压缩消息之后的全是 所以就不需要压缩消息长度取解析了 */
            /** 解压缩后的数据 */
            byte[] data = ZipUtils.ungzip(ByteHexUtils.subbyte(messageBodyBytes, 4));
            dataService.compressData(phone, data);
        });
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0);
    }
}
