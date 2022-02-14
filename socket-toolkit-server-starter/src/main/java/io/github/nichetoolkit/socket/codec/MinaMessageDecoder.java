package io.github.nichetoolkit.socket.codec;

import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * <p>MinaMessageDecoder</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class MinaMessageDecoder extends CumulativeProtocolDecoder {

    private MessageCoder messageCoder;

    public MinaMessageDecoder(MessageCoder messageCoder) {
        this.messageCoder = messageCoder;
    }

    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer input, ProtocolDecoderOutput output) throws Exception {
        MinaBufferCache bufferCache = new MinaBufferCache(ioSession, input, output);
        return messageCoder.decode(bufferCache);
    }
}
