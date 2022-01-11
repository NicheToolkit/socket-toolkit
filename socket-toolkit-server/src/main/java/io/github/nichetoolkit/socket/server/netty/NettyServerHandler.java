package io.github.nichetoolkit.socket.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * <p>NettyServerHandler</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("id: {}, message: {}", ctx.name(), cause.getMessage());
        log.warn(cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        byte[] originData  = (byte[]) message;
        log.debug("id: {}, message: {}", ctx.name(), Arrays.toString(originData));
    }
}
