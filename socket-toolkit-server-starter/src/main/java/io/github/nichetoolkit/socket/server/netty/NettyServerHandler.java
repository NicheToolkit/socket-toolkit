package io.github.nichetoolkit.socket.server.netty;

import io.github.nichetoolkit.socket.server.handler.SocketServerHandler;
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

    private SocketServerHandler socketServerHandler;

    public NettyServerHandler(SocketServerHandler socketServerHandler) {
        this.socketServerHandler = socketServerHandler;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.debug("session registered, name: {} ", ctx.name());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        log.debug("session unregistered, name: {} ", ctx.name());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug("session channel active, name: {} ", ctx.name());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.debug("session channel inactive, name: {} ", ctx.name());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        log.debug("session channel read complete, name: {} ", ctx.name());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        log.debug("session user event, name: {}", ctx.name());
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        log.debug("session channel writability changed, name: {}", ctx.name());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("name: {}, message: {}", ctx.name(), cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        byte[] originData = (byte[]) message;
        log.debug("name: {}, message: {}", ctx.name(), Arrays.toString(originData));
        socketServerHandler.handle(ctx, message);
    }
}
