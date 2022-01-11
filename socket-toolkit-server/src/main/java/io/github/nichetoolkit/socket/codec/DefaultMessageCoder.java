package io.github.nichetoolkit.socket.codec;

/**
 * <p>DefaultMessageCoder</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class DefaultMessageCoder implements MessageCoder {

    public DefaultMessageCoder() {
    }

    @Override
    public boolean decode(BufferCache bufferCache) {
        return false;
    }

    @Override
    public byte[] encode(byte[] bytes) {
        return bytes;
    }
}
