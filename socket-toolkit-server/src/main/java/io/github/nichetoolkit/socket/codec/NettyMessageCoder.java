package io.github.nichetoolkit.socket.codec;

/**
 * <p>NettyMessageCoder</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public interface NettyMessageCoder {

    boolean decode(NettyBufferCache bufferCache);

    byte[] encode(byte[] bytes);
}
