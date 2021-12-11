package io.github.nichetoolkit.socket.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * <p>NettyBufferCache</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class NettyBufferCache {
    private ChannelHandlerContext ctx;
    private ByteBuf message;
    private List<Object> nettyOut;

    public NettyBufferCache(ChannelHandlerContext ctx, ByteBuf message, List<Object> out) {
        this.ctx = ctx;
        this.message = message;
        this.nettyOut = out;
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

    public List<Object> getNettyOut() {
        return nettyOut;
    }

    public void setNettyOut(List<Object> nettyOut) {
        this.nettyOut = nettyOut;
    }

    public int remaining() {
        return message.readableBytes();
    }

    public void mark() {
        message.markReaderIndex();
    }

    public byte get() {
        return message.readByte();
    }

    public void reset() {
        message.resetReaderIndex();
    }

    public void get(byte[] buf) {
        message.readBytes(buf);
    }

    public String getRemoteAddress() {
        return ctx.channel().remoteAddress().toString();
    }

    public void write(byte[] buf) {
        nettyOut.add(buf);

    }
}
