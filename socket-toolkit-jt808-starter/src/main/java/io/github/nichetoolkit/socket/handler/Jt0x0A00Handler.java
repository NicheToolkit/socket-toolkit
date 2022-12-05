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
 * <p>Jt0x0A00Handler 终端RSA公钥</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Slf4j
@SocketPackage(messageId = 0x0A00)
public class Jt0x0A00Handler implements SocketPackageHandler {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private Jt808DataService dataService;

    @Override
    public byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes) {
        log.info("[Jt0x0A00] 0A00 [终端RSA公钥] terminal rsa");
        /** 终端 RSA 公钥 { e, n } */
        byte[] e = ByteHexUtils.subbyte(messageBodyBytes,0,4);
        byte[] n = ByteHexUtils.subbyte(messageBodyBytes,4);
        int maxLen = 128;
        if(n.length == maxLen){
            /** 存储加密信息 以便收到数据后解密 */
            threadPoolExecutor.execute(()-> {
                String phone = ByteHexUtils.parseHex(phoneBytes);
                dataService.terminalRsa(phone, e, n);
            });
            return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x00);
        }else{
            return Jt808Utils.buildJt8001(phoneBytes, flowIdBytes, messageIdBytes, (byte) 0x01);
        }
    }
}
