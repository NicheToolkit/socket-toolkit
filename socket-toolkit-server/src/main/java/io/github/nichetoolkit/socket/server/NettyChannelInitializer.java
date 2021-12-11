package io.github.nichetoolkit.socket.server;

import io.github.nichetoolkit.rest.util.common.GeneralUtils;
import io.github.nichetoolkit.socket.codec.NettyMessageDecoder;
import io.github.nichetoolkit.socket.codec.NettyMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.springframework.context.ApplicationContext;

/**
 * <p>NettyChannelInitializer</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private NettyMessageDecoder messageDecoder;
    private NettyMessageEncoder messageEncoder;
    private NettyServerHandler serverHandler;

    public NettyChannelInitializer(ApplicationContext applicationContext) {
        this.messageDecoder = applicationContext.getBean(NettyMessageDecoder.class);
        this.messageEncoder = applicationContext.getBean(NettyMessageEncoder.class);
        this.serverHandler = applicationContext.getBean(NettyServerHandler.class);
    }

    public NettyChannelInitializer(NettyMessageDecoder messageDecoder, NettyMessageEncoder messageEncoder, NettyServerHandler serverHandler) {
        this.messageDecoder = messageDecoder;
        this.messageEncoder = messageEncoder;
        this.serverHandler = serverHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        if (GeneralUtils.isNotEmpty(messageDecoder)) {
            socketChannel.pipeline().addLast(messageDecoder);
        }
        if (GeneralUtils.isNotEmpty(messageEncoder)) {
            socketChannel.pipeline().addLast(messageEncoder);
        }
        if (GeneralUtils.isNotEmpty(serverHandler)) {
            socketChannel.pipeline().addLast(serverHandler);
        }
    }
}
