package io.github.nichetoolkit.socket.server;

import io.github.nichetoolkit.rest.util.common.GeneralUtils;
import io.github.nichetoolkit.socket.configure.SocketServerProperties;
import io.github.nichetoolkit.socket.constant.SocketServerConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * <p>SocketNettyServer</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
public class DefaultNettyServer implements ApplicationContextAware, SocketServer {
    private static ApplicationContext applicationContext;
    private volatile boolean isRunning = false;
    private static Channel serverChannel;
    private static EventLoopGroup masterGroup;
    private static EventLoopGroup slaveGroup;
    private SocketServerProperties properties;
    private String name;
    private Integer port;
    private NettyChannelInitializer channelInitializer;

    public DefaultNettyServer(SocketServerProperties properties) {
        this.properties = properties;
        this.name = properties.getName();
        this.port = properties.getPort();
        this.channelInitializer = applicationContext.getBean(NettyChannelInitializer.class);
        if (GeneralUtils.isEmpty(this.channelInitializer)) {
            this.channelInitializer = new NettyChannelInitializer(applicationContext){};
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        DefaultNettyServer.applicationContext = applicationContext;
    }

    @Override
    public synchronized boolean start() {
        try {
            if (serverChannel != null) {
                return serverChannel.isActive();
            }
            String protocol = properties.getProtocol();
            boolean isTCP = SocketServerConstants.TCP.equals(protocol);
            masterGroup = new NioEventLoopGroup(properties.getMasterSize());
            ChannelFuture channelFuture;
            if (isTCP) {
                slaveGroup = new NioEventLoopGroup(properties.getSlaveSize());
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(masterGroup, slaveGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, properties.getBacklog())
                        .childOption(ChannelOption.SO_KEEPALIVE, properties.getKeepalive())
                        .childOption(ChannelOption.TCP_NODELAY, properties.getTcpNoDelay())
                        .childHandler(channelInitializer);
                /* 内存泄漏检测 开发推荐PARANOID 线上SIMPLE */
                ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);
                channelFuture = serverBootstrap.bind(port).sync();

            } else {
                Bootstrap serverBootstrap = new Bootstrap();
                serverBootstrap.group(masterGroup)
                        .channel(NioDatagramChannel.class)
                        .option(ChannelOption.SO_BROADCAST, true)
                        .handler(channelInitializer);
                channelFuture = serverBootstrap.bind(port).sync();
            }
            serverChannel = channelFuture.channel();
            log.info("Server has started successful!, name: {}, port: {}", name, port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException exception) {
            log.error("Server has started with error!, error: {}", exception.getMessage());
        } finally {
            stop();
        }
        return true;
    }

    @Override
    public synchronized boolean stop() {
        if (!this.isRunning) {
            log.info(" Server has stopped successful!, name: {}, port: {}", name, port);
        }
        this.isRunning = false;
        serverChannel.close();
        if (masterGroup != null) {
            Future future = masterGroup.shutdownGracefully();
            if (!future.isSuccess()) {
                log.warn("Boss group can not stop! error: {}", future.cause());
                return false;
            }
        }
        if (slaveGroup != null) {
            Future future = slaveGroup.shutdownGracefully();
            if (!future.isSuccess()) {
                log.warn("Worker group can not stop! error: {}", future.cause());
                return false;
            }
        }
        log.info(" Server has stopped successful!, name: {}, port: {}", name, port);
        return true;
    }


}
