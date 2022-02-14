package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.model.Jt808Location;
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
 * <p>Jt0x0801Handler 多媒体数据上传</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0801)
public class Jt0x0801Handler implements SocketPackageHandler {
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0800] 0800 [多媒体事件信息上传] media event upload");
        log.info("0801 多媒体数据上传 MediaInfoUpload");
        threadPoolExecutor.execute(() -> {
            String phone = ByteHexUtils.parseHex(phoneBytes);
            /**  多媒体信息, 位置信息, 多媒体数据包 */
            byte[] mediaInfoData, locationData, mediaData;
            mediaInfoData = ByteHexUtils.subbyte(messageBodyBytes, 0, 8);
            locationData = ByteHexUtils.subbyte(messageBodyBytes, 8, 36);
            if (phoneBytes.length == 10) {
                /**  2019 版本存在 */
                mediaData = ByteHexUtils.subbyte(messageBodyBytes, 36);
            } else {
                /** 2013 版本存在 2011版本不存在 */
                boolean isLocationData = Jt808Utils.verifyLocation(locationData);
                if (isLocationData) {
                    mediaData = ByteHexUtils.subbyte(messageBodyBytes, 36);
                } else {
                    locationData = null;
                    mediaData = ByteHexUtils.subbyte(messageBodyBytes, 8);
                }
            }
            /** 存储多媒体信息 */
            Jt808Media jt808Media = AnalyzeUtils.analyzeMedia(mediaInfoData);
            dataService.mediaInfo(phone, jt808Media);
            if (locationData != null) {
                /** 存储定位数据 */
                Jt808Location jt808Location = AnalyzeUtils.analyzeLocation(locationData);
                dataService.terminalLocation(phone, jt808Location, jt808Media.getMediaId());
            }
            /** 存储多媒体数据包 */
            dataService.mediaPackage(phone, mediaData, jt808Media.getMediaId());
        });
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0);
    }
}
