package io.github.nichetoolkit.socket.codec;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>NettyMessageEncoder</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyMessageEncoder extends MessageToMessageEncoder<byte[]> {

    private MessageCoder messageCoder;

    public NettyMessageEncoder(MessageCoder messageCoder) {
        this.messageCoder = messageCoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] message, List<Object> out) throws Exception {
        byte[] encode = messageCoder.encode(message);
        out.add(Unpooled.wrappedBuffer(encode));
    }
}
