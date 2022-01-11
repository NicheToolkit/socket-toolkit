package io.github.nichetoolkit.socket.server.mina;

import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * <p>MinaMessageCoder</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class MinaCodecFactory implements ProtocolCodecFactory {
    private ProtocolDecoder decoder;
    private ProtocolEncoder encoder;

    public MinaCodecFactory( ProtocolDecoder decoder, ProtocolEncoder encoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }
}
