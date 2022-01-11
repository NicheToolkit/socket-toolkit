package io.github.nichetoolkit.socket.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * <p>MinaBufferCache</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class MinaBufferCache implements BufferCache {

    private IoSession session;
    private IoBuffer input;
    private ProtocolDecoderOutput output;

    public MinaBufferCache(IoSession session, IoBuffer input, ProtocolDecoderOutput output) {
        this.session = session;
        this.input = input;
        this.output = output;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public IoBuffer getInput() {
        return input;
    }

    public void setInput(IoBuffer input) {
        this.input = input;
    }

    public ProtocolDecoderOutput getOutput() {
        return output;
    }

    public void setOutput(ProtocolDecoderOutput output) {
        this.output = output;
    }

    @Override
    public int remaining() {
        return input.remaining();
    }

    @Override
    public void mark() {
        input.mark();
    }

    @Override
    public byte read() {
        return input.get();
    }

    @Override
    public void reset() {
        input.reset();
    }

    @Override
    public void read(byte[] buffer) {
        input.get(buffer);
    }

    @Override
    public String address() {
        return session.getRemoteAddress().toString();
    }

    @Override
    public void write(byte[] buffer) {
        output.write(buffer);
    }
}
