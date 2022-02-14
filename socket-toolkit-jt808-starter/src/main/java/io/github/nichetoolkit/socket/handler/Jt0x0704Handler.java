package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.constant.SocketJt808Constants;
import io.github.nichetoolkit.socket.model.Jt808Location;
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
 * <p>Jt0x0704Handler 定位数据批量上传</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0704)
public class Jt0x0704Handler implements SocketPackageHandler {
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0702] 0702 [定位数据批量上传] location data upload");
        threadPoolExecutor.execute(() -> {
            String phone = ByteHexUtils.parseHex(phoneBytes);
            /** 循环分析定位上报数据 */
            for (int pos = 3, len; pos < messageBodyBytes.length; pos += len + SocketJt808Constants.NUMBER_2) {
                len = ByteHexUtils.parseTwoInt(ByteHexUtils.subbyte(messageBodyBytes, pos, pos + 2));
                byte[] data = ByteHexUtils.subbyte(messageBodyBytes, pos + 2, pos + 2 + len);
                Jt808Location jt808Location = AnalyzeUtils.analyzeLocation(data);
                dataService.terminalLocation(phone, jt808Location, null);
            }
        });
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0);
    }
}
