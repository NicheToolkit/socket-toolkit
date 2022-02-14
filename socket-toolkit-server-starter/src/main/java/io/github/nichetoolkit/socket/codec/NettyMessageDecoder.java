package io.github.nichetoolkit.socket.codec;

import io.github.nichetoolkit.socket.constant.SocketServerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>NettyMessageDecoder</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final AttributeKey<ByteBuf> BUFFER_CACHE = AttributeKey.valueOf(SocketServerConstants.BUFFER);

    private MessageCoder messageCoder;

    public NettyMessageDecoder(MessageCoder messageCoder) {
        this.messageCoder = messageCoder;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf message, List<Object> out) throws Exception {
        String data = ByteBufUtil.hexDump(message);
        log.info(" ip: {}, hex: {}", ctx.channel().remoteAddress(), data);
        boolean usingSessionBuffer = true;
        ByteBuf buf = ctx.channel().attr(BUFFER_CACHE).get();
        if (buf != null) {
            boolean appended = false;
            if (buf.writableBytes() > 0) {
                try {
                    buf.writeBytes(ByteBufUtil.getBytes(message));
                    appended = true;
                } catch (IndexOutOfBoundsException | IllegalStateException e) {
                    log.warn(e.getMessage());
                }
            }

            if (appended) {
                buf.nioBuffer().flip();
            } else {
                buf.nioBuffer().flip();
                ByteBuf newBuf = ByteBufAllocator.DEFAULT.buffer(buf.readableBytes() + message.readableBytes());
                newBuf.writeBytes(ByteBufUtil.getBytes(buf));
                newBuf.writeBytes(ByteBufUtil.getBytes(message));
                buf = newBuf;
                ctx.channel().attr(BUFFER_CACHE).set(newBuf);
            }
        } else {
            buf = message;
            usingSessionBuffer = false;
        }

        do {
            int oldPos = buf.readerIndex();
            boolean decoded = this.doDecode(ctx, buf, out);
            if (!decoded) {
                break;
            }

            if (buf.readerIndex() == oldPos) {
                throw new IllegalStateException("doDecode() can't return true when buffer is not consumed.");
            }
        } while (buf.readableBytes() > 0);

        if (buf.readableBytes() > 0) {
            if (usingSessionBuffer && buf.writableBytes() > 0) {
                buf.nioBuffer().compact();
            } else {
                this.cache(buf, ctx);
            }
        } else if (usingSessionBuffer) {
            this.remove(ctx);
        }

    }

    public void remove(ChannelHandlerContext ctx) {
        ctx.channel().attr(BUFFER_CACHE).set(null);
    }

    public void cache(ByteBuf buf, ChannelHandlerContext ctx) {
        ctx.channel().attr(BUFFER_CACHE).set(buf);
    }

    public boolean doDecode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
         NettyBufferCache bufferCache = new NettyBufferCache(ctx, msg, out);
        return messageCoder.decode(bufferCache);
    }
}
