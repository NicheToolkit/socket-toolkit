package io.github.nichetoolkit.socket.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * <p>NettyBufferCache</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class NettyBufferCache implements BufferCache {
    private ChannelHandlerContext ctx;
    private ByteBuf message;
    private List<Object> output;

    public NettyBufferCache(ChannelHandlerContext ctx, ByteBuf message, List<Object> output) {
        this.ctx = ctx;
        this.message = message;
        this.output = output;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ByteBuf getMessage() {
        return message;
    }

    public void setMessage(ByteBuf message) {
        this.message = message;
    }

    public List<Object> getOutput() {
        return output;
    }

    public void setOutput(List<Object> output) {
        this.output = output;
    }

    @Override
    public int remaining() {
        return message.readableBytes();
    }

    @Override
    public void mark() {
        message.markReaderIndex();
    }

    @Override
    public byte read() {
        return message.readByte();
    }

    @Override
    public void reset() {
        message.resetReaderIndex();
    }

    @Override
    public void read(byte[] buffer) {
        message.readBytes(buffer);
    }

    @Override
    public String address() {
        return ctx.channel().remoteAddress().toString();
    }

    @Override
    public void write(byte[] buffer) {
        output.add(buffer);
    }
}
