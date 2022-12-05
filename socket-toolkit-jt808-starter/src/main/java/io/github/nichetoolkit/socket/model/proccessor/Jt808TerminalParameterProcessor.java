package io.github.nichetoolkit.socket.model.proccessor;

/**
 * <p>TerminalParameterProcessor</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
public interface Jt808TerminalParameterProcessor {
    /**
     * 终端参数处理
     * @param id 参数ID 16进制字符串
     * @param bytes 参数对应数据
     */
    void process(String id, byte[] bytes);
}
