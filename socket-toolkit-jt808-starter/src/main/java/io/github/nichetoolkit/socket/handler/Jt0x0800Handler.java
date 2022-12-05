package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.model.Jt808Media;
import io.github.nichetoolkit.socket.server.SocketPackage;
import io.github.nichetoolkit.socket.server.handler.SocketPackageHandler;
import io.github.nichetoolkit.socket.service.Jt808DataService;
import io.github.nichetoolkit.socket.util.AnalyzeUtils;
import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Jt0x0800Handler 多媒体事件信息上传</p>
 * 这个信息是没有定位数据的
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0800)
public class Jt0x0800Handler implements SocketPackageHandler {
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0800] 0800 [多媒体事件信息上传] media event upload");
        /** 保存多媒体信息到数据库 但没有实体文件 */
        threadPoolExecutor.execute(() -> {
            String phone = ByteHexUtils.parseHex(phoneBytes);
            Jt808Media jt808Media = AnalyzeUtils.analyzeMedia(messageBodyBytes);
            dataService.mediaInfo(phone, jt808Media);
        });
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0);
    }
}
