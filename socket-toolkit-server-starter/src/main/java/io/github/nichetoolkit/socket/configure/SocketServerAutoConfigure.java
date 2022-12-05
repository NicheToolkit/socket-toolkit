package io.github.nichetoolkit.socket.configure;

import io.github.nichetoolkit.rest.RestException;
import io.github.nichetoolkit.rest.util.GeneralUtils;
import io.github.nichetoolkit.socket.codec.*;
import io.github.nichetoolkit.socket.constant.SocketServerConstants;
import io.github.nichetoolkit.socket.server.*;
import io.github.nichetoolkit.socket.server.handler.SocketHandlerManager;
import io.github.nichetoolkit.socket.server.handler.SocketMessageHandler;
import io.github.nichetoolkit.socket.server.handler.SocketServerHandler;
import io.github.nichetoolkit.socket.server.mina.DefaultMinaServer;
import io.github.nichetoolkit.socket.server.mina.MinaCodecFactory;
import io.github.nichetoolkit.socket.server.mina.MinaServerHandler;
import io.github.nichetoolkit.socket.server.netty.DefaultNettyServer;
import io.github.nichetoolkit.socket.server.ServerThreadFactory;
import io.github.nichetoolkit.socket.server.netty.NettyChannelInitializer;
import io.github.nichetoolkit.socket.server.netty.NettyServerHandler;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>SocketServerAutoConfigure</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Slf4j
@Configuration
@ComponentScan(value = "io.github.nichetoolkit.socket")
public class SocketServerAutoConfigure  {

    @Autowired
    public SocketServerAutoConfigure() {
        log.debug("================= socket-server-auto-configure initiated ！ ===================");
    }

    @Bean
    @ConditionalOnMissingBean(ThreadPoolExecutor.class)
    public ThreadPoolExecutor threadPoolExecutor(SocketServerProperties properties) {
        return new ThreadPoolExecutor(
                properties.getThreadPool().getCorePoolSize(),
                properties.getThreadPool().getMaxPoolSize(),
                properties.getThreadPool().getKeepaliveTime(),
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new ServerThreadFactory(SocketServerConstants.NETTY_THREAD_PREFIX)
        );
    }

    @Bean
    @ConditionalOnMissingBean(SocketHandlerManager.class)
    public SocketHandlerManager socketHandlerManager() {
        return new SocketHandlerManager();
    }

    @Bean
    @ConditionalOnMissingBean(MessageCoder.class)
    public MessageCoder messageCoder() {
        return new DefaultMessageCoder();
    }

    @Bean
    @ConditionalOnMissingBean(SocketServerHandler.class)
    public SocketServerHandler socketServerHandler() {
        return new SocketServerHandler();
    }

    @Bean
    @ConditionalOnMissingBean(SocketMessageHandler.class)
    public SocketMessageHandler socketMessageHandler() {
        return new SocketMessageHandler() {

            @Override
            public void beforeMessage(Object message) throws RestException {

            }

            @Override
            public void doHandle(Object session, Object message) throws RestException {

            }

            @Override
            public void afterSession(Object session) throws RestException {

            }
        };
    }


    @Configuration
    @ConditionalOnProperty(value = "nichetoolkit.socket.server.server-type", havingValue = "NETTY")
    static class NettyServerAutoConfigure {

        public NettyServerAutoConfigure() {
            log.debug("================= netty-server-auto-configure initiated ！ ===================");
        }

        @Bean
        @ConditionalOnMissingBean(NettyMessageDecoder.class)
        public NettyMessageDecoder messageDecoder(MessageCoder messageCoder) {
            return new NettyMessageDecoder(messageCoder);
        }

        @Bean
        @ConditionalOnMissingBean(NettyMessageEncoder.class)
        public NettyMessageEncoder messageEncoder(MessageCoder messageCoder) {
            return new NettyMessageEncoder(messageCoder);
        }

        @Bean
        @Primary
        @ConditionalOnMissingBean(NettyServerHandler.class)
        public NettyServerHandler serverHandler(SocketServerHandler socketServerHandler) {
            return new NettyServerHandler(socketServerHandler);
        }

        @Bean
        @ConditionalOnMissingBean(NettyChannelInitializer.class)
        public NettyChannelInitializer channelInitializer(NettyMessageDecoder messageDecoder, NettyMessageEncoder messageEncoder,NettyServerHandler serverHandler) {
            return new NettyChannelInitializer(messageDecoder,messageEncoder,serverHandler);
        }

        @Bean
        @Primary
        @ConditionalOnMissingBean(SocketServer.class)
        public SocketServer nettyServer(SocketServerProperties properties,NettyChannelInitializer channelInitializer) {
            DefaultNettyServer defaultNettyServer = new DefaultNettyServer(properties,channelInitializer);
            ServerManager.add(properties.getName(),defaultNettyServer);
            return defaultNettyServer;
        }

    }

    @Configuration
    @ConditionalOnProperty(value = "nichetoolkit.socket.server.server-type", havingValue = "MINA")
    static class MinaServerAutoConfigure {

        public MinaServerAutoConfigure() {
            log.debug("================= mina-server-auto-configure initiated ！ ===================");
        }

        @Bean
        @ConditionalOnMissingBean(ExecutorFilter.class)
        public ExecutorFilter executorFilter(SocketServerProperties properties) {
            return new ExecutorFilter(
                    properties.getThreadPool().getCorePoolSize(),
                    properties.getThreadPool().getMaxPoolSize(),
                    properties.getThreadPool().getKeepaliveTime(),
                    TimeUnit.MILLISECONDS,
                    new ServerThreadFactory(SocketServerConstants.NETTY_THREAD_PREFIX)
            );
        }

        @Bean
        @ConditionalOnMissingBean(MinaMessageDecoder.class)
        public MinaMessageDecoder messageDecoder(MessageCoder messageCoder) {
            return new MinaMessageDecoder(messageCoder);
        }

        @Bean
        @ConditionalOnMissingBean(MinaMessageEncoder.class)
        public MinaMessageEncoder messageEncoder(MessageCoder messageCoder) {
            return new MinaMessageEncoder(messageCoder);
        }

        @Bean
        @ConditionalOnMissingBean(MinaCodecFactory.class)
        public MinaCodecFactory codecFactory(MinaMessageDecoder messageDecoder,MinaMessageEncoder messageEncoder) {
            return new MinaCodecFactory(messageDecoder,messageEncoder);
        }

        @Bean
        @Primary
        @ConditionalOnMissingBean(MinaServerHandler.class)
        public MinaServerHandler serverHandler(SocketServerHandler socketServerHandler, SocketServerProperties properties) {
            return new MinaServerHandler(socketServerHandler,properties);
        }

        @Bean
        @Primary
        @ConditionalOnMissingBean(SocketServer.class)
        public SocketServer minaServer(SocketServerProperties properties, ExecutorFilter executorFilter, MinaCodecFactory codecFactory, MinaServerHandler serverHandler) {
            DefaultMinaServer defaultMinaServer = new DefaultMinaServer(properties,executorFilter,codecFactory,serverHandler);
            ServerManager.add(properties.getName(),defaultMinaServer);
            return defaultMinaServer;
        }
    }


}
