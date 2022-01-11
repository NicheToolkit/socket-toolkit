package io.github.nichetoolkit.socket.codec;

import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * <p>MinaMessageEncoder</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class MinaMessageEncoder extends ProtocolEncoderAdapter {

    private MessageCoder messageCoder;

    public MinaMessageEncoder(MessageCoder messageCoder) {
        this.messageCoder = messageCoder;
    }

    @Override
    public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput output) throws Exception {
        byte[] buf = (byte[]) message;
        byte[] buffer = messageCoder.encode(buf);
        output.write(IoBuffer.wrap(buffer));
    }
}
