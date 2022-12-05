package io.github.nichetoolkit.socket.server.handler;

/**
 * <p>SocketPackageHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public interface SocketPackageHandler {

    /**
     * 解析完成后对数据进行处理的方法
     * @param phoneBytes       终端号码字节数组
     * @param flowIdBytes      平台流水号
     * @param messageIdBytes   消息ID
     * @param messageBodyBytes 消息体
     * @return byte[] 返回应答消息
     */
    byte[] handle(byte[] phoneBytes, byte[] flowIdBytes, byte[] messageIdBytes, byte[] messageBodyBytes);
}
