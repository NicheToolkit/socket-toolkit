package io.github.nichetoolkit.socket.codec;

/**
 * <p>BufferCache</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public interface BufferCache {

    int remaining();

    String address();

    void mark();

    void reset();

    byte read();

    void read(byte[] buffer);

    void write(byte[] buffer);
}
