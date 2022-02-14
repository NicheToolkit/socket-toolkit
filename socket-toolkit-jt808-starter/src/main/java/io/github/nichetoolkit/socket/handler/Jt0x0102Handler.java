package io.github.nichetoolkit.socket.handler;

import io.github.nichetoolkit.socket.configure.SocketJt808Properties;
import io.github.nichetoolkit.socket.constant.SocketJt808Constants;
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
 * <p>Jt0x0102Handler 终端鉴权 </p>
 * 使用注册的鉴权码进行鉴权鉴权之前终端不能发送数据 鉴权码最好是随机的 要求终端每次链接请求一次
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0102)
public class Jt0x0102Handler implements SocketPackageHandler {

    @Autowired
    private SocketJt808Properties jt808Properties;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808DataService dataService;

    @Autowired
    private Jt808CacheService cacheService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0102] 0102 [终端鉴权] terminal authentication");
        int version;
        if (phoneBytes.length == SocketJt808Constants.PHONE_2019_LENGTH) {
            version = SocketJt808Constants.VERSION_2019;
        } else {
            version = SocketJt808Constants.VERSION_20132011;
        }
        String phone = ByteHexUtils.parseHex(phoneBytes);

        /** 获取鉴权码 */
        String authId;

        if (version == SocketJt808Constants.VERSION_2019) {
            int len = messageBodyBytes[0];
            authId = Jt808Utils.parseGBK(ByteHexUtils.subbyte(messageBodyBytes, 1, 1 + len));
        } else {
            authId = Jt808Utils.parseGBK(messageBodyBytes);
        }
        String oriAuthId;
        if (jt808Properties.getUseConstantAuth()) {
            oriAuthId = jt808Properties.getConstantAuth();
        } else {
            oriAuthId = cacheService.getAuth(phone);
        }

        byte result;
        if (oriAuthId != null && oriAuthId.equals(authId)) {
            /** 鉴权成功 */
            result = 0;
            log.info("[Jt0x0102] 0102 [终端鉴权] <<<<<<<<鉴权成功!!!");
            /** 成功后保存鉴权信息 */
            if (version == SocketJt808Constants.VERSION_2019) {
                threadPoolExecutor.execute(() -> {
                    byte[] imei = ByteHexUtils.subbyte(messageBodyBytes, messageBodyBytes[0] + 1, messageBodyBytes[0] + 16);
                    byte[] softVersion = ByteHexUtils.subbyte(messageBodyBytes, messageBodyBytes[0] + 16, messageBodyBytes[0] + 36);
                    dataService.terminalAuth(
                            phone,
                            oriAuthId,
                            Jt808Utils.parseAscII(imei),
                            Jt808Utils.parseAscII(softVersion)
                    );
                });
            } else {
                threadPoolExecutor.execute(() -> dataService.terminalAuth(
                        phone,
                        oriAuthId,
                        null,
                        null
                ));
            }
        } else {
            log.info("[Jt0x0102] 0102 [终端鉴权] >>>>>>>>>鉴权失败!!!");
            /** 鉴权失败 */
            result = 1;
        }
        return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, result);
    }
}
