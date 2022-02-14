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
 * <p>Jt0x0608Handler 2019 新增 查询区域或者线路数据应答</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0608)
public class Jt0x0608Handler implements SocketPackageHandler {
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0608] 0608 [查询区域或者线路数据应答] search area or route data answer");
        String phone = ByteHexUtils.parseHex(phoneBytes);
        /** 消息体中没有终端对应平台下发指令的流水号 所以指定流水号为 -1 */
        int flowId = -1;
        /** 保存命令到相应的下发指令 */
        threadPoolExecutor.execute(() -> dataService.terminalAnswer(phone, flowId, "8608", "0608", messageBodyBytes));
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x00);
    }
}
