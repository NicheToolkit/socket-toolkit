package io.github.nichetoolkit.socket.codec;

import io.github.nichetoolkit.socket.util.ByteHexUtils;
import io.github.nichetoolkit.socket.util.Jt808Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>SocketJt808MessageCoder</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class SocketJt808MessageCoder implements MessageCoder {

    private static final byte MSG_BROKER = 0x7E;
    /**
     * 含标识位
     */
    private static final int MSG_MIN_LEN = 15;
    private static final int MSG_MIN_LEN_2019 = 20;
    /**
     * 含标识位
     */
    private static final int MSG_MIN_LEN_WITH_PKG = 19;
    private static final int MSG_MIN_LEN_WITH_PKG_2019 = 24;
    private static final int MAX_READ_LEN = 1024 * 10;

    @Override
    public boolean decode(BufferCache bufferCache) {
        /** 有数据时，读取前 8 字节判断消息长度 */
        if (bufferCache.remaining() > 0) {
            /** 标记初始位置 */
            bufferCache.mark();
            /** 读取一字节 检查是否标识位 */
            byte byteBuf = bufferCache.read();
            if (byteBuf == MSG_BROKER) {
                /** 设置一个长度缓存 */
                int pos = 1;
                /** 是标识位 循环读取 直到 下一个标识位 为止 */
                for (int i = 0; i < bufferCache.remaining(); ) {
                    byteBuf = bufferCache.read();
                    pos++;
                    if (byteBuf == MSG_BROKER) {
                        /** 读取到 下一个标识位 并且重置缓存 */
                        break;
                    }
                }
                /** 循环终结 但是 还是没找到最后的标识位 继续读取 重新解析 */
                if (byteBuf != MSG_BROKER) {
                    /** FIXME 如果只有一个 标识位 会导致一直读取的问题 需要设置一个限定长度 读取超过这个长度 就直接丢弃 */
                    /** 目前设置成 10K 以后会加入配置 */
                    if (pos < MAX_READ_LEN) {
                        bufferCache.reset();
                    }
                    return false;
                }
                /** 小于最小包长度 截断 重新读取 剩余的 字节 */
                if (pos < MSG_MIN_LEN) {
                    log.warn("data is too short ... drop !");
                    return bufferCache.remaining() > 0;
                }
                /** 重置缓存 */
                bufferCache.reset();
                /** 读取缓存 */
                byte[] packageBuf = new byte[pos];
                bufferCache.read(packageBuf);
                log.trace("origin data : {}", ByteHexUtils.parseHex(packageBuf));
                /** 转义 转义后的值 已经去掉了 标识位 */
                packageBuf = Jt808Utils.retrans(packageBuf);
                log.trace("trans data : {}", ByteHexUtils.parseHex(packageBuf));
                /** 校验失败 丢掉当前包 继续读取剩余的字节 */
                boolean verify = Jt808Utils.verify(packageBuf);
                if (!verify) {
                    log.warn("verify failed {}", bufferCache.address());
                    return bufferCache.remaining() > 0;
                }
                /** 校验成功 检查 长度 并输出到下一步操作 */
                byte[] body = ByteHexUtils.subbyte(packageBuf, 2, 4);
                int sizeBuf = Jt808Utils.messageBodyLength(body);
                boolean version2019 = Jt808Utils.isVersion2019(body);
                boolean isSlice = Jt808Utils.isSlicePackage(body);
                int size;
                if (isSlice) {
                    if (version2019) {
                        size = sizeBuf + MSG_MIN_LEN_WITH_PKG_2019 - 2;
                    } else {
                        size = sizeBuf + MSG_MIN_LEN_WITH_PKG - 2;
                    }
                } else {
                    if (version2019) {
                        size = sizeBuf + MSG_MIN_LEN_2019 - 2;
                    } else {
                        size = sizeBuf + MSG_MIN_LEN - 2;
                    }
                }

                /** 检查长度 */
                if (size == packageBuf.length) {
                    /** 长度符合 输出 */
                    log.trace("handle data : {}", ByteHexUtils.parseHex(packageBuf));
                    /** 带有校验位的完整消息 */
                    bufferCache.write(packageBuf);
                    return bufferCache.remaining() > 0;
                }
                /** 长度不符合 */
                log.warn("wrong data length expected {}, real {} drop !", size, packageBuf.length);
                return bufferCache.remaining() > 0;
            } else {
                log.warn("wrong data structure {}", bufferCache.address());
                for (int i = 0; i < bufferCache.remaining(); ) {
                    if (bufferCache.read() == MSG_BROKER) {
                        /** 如果发送数据不正确 找到下一个0x7e 截断后 再读取一遍 */
                        return true;
                    }
                }
            }
        }
        /** 处理成功，让父类进行接收下个包 */
        return false;
    }

    @Override
    public byte[] encode(byte[] bytes) {
        return Jt808Utils.trans(Jt808Utils.addVerify(bytes));
    }
}
