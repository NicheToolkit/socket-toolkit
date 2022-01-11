package io.github.nichetoolkit.socket.codec;

/**
 * <p>MessageCoder</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public interface MessageCoder {

    boolean decode(BufferCache bufferCache);

    byte[] encode(byte[] bytes);
}
