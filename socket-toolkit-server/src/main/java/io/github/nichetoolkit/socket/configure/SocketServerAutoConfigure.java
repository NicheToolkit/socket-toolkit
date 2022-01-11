package io.github.nichetoolkit.socket.configure;

import io.github.nichetoolkit.rest.util.common.GeneralUtils;
import io.github.nichetoolkit.socket.codec.*;
import io.github.nichetoolkit.socket.constant.SocketServerConstants;
import io.github.nichetoolkit.socket.server.*;
import io.github.nichetoolkit.socket.server.mina.DefaultMinaServer;
import io.github.nichetoolkit.socket.server.mina.MinaCodecFactory;
import io.github.nichetoolkit.socket.server.mina.MinaServerHandler;
import io.github.nichetoolkit.socket.server.netty.DefaultNettyServer;
import io.github.nichetoolkit.socket.server.ServerThreadFactory;
import io.github.nichetoolkit.socket.server.netty.NettyChannelInitializer;
import io.github.nichetoolkit.socket.server.netty.NettyServerHandler;
import org.apache.mina.core.service.IoHandler;
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
@ConditionalOnProperty(value = "nichetoolkit.socket.server.enabled", havingValue = "true")
public class SocketServerAutoConfigure implements ApplicationListener<ApplicationStartedEvent> {
    private final SocketServerProperties properties;

    @Autowired
    public SocketServerAutoConfigure(SocketServerProperties properties) {
        this.properties = properties;
    }


    @Bean
    @ConditionalOnMissingBean(MessageCoder.class)
    public MessageCoder messageCoder() {
        return new DefaultMessageCoder();
    }


    @Configuration
    @ConditionalOnProperty(value = "nichetoolkit.socket.server.server-type", havingValue = "NETTY")
    static class NettyServerAutoConfigure {

        private final SocketServerProperties properties;

        public NettyServerAutoConfigure(SocketServerProperties properties) {
            this.properties = properties;
            log.info("================= netty-server-auto-configure initiated ！ ===================");
        }

        @Bean
        @ConditionalOnMissingBean(ThreadPoolExecutor.class)
        public ThreadPoolExecutor threadPoolExecutor() {
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
        public NettyServerHandler serverHandler() {
            return new NettyServerHandler(){};
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

        private final SocketServerProperties properties;

        public MinaServerAutoConfigure(SocketServerProperties properties) {
            this.properties = properties;
            log.info("================= mina-server-auto-configure initiated ！ ===================");
        }

        @Bean
        @ConditionalOnMissingBean(ExecutorFilter.class)
        public ExecutorFilter executorFilter() {
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
        public MinaServerHandler serverHandler(SocketServerProperties properties) {
            return new MinaServerHandler(properties);
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

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        if (!properties.getEnabled()) {
            log.debug("socket server is disabled!");
            return;
        }
        String serverName = properties.getName();
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        SocketServer server = ServerManager.server(serverName);
        if (GeneralUtils.isEmpty(server) && !applicationContext.containsBean(serverName)) {
            log.error("no netty server instance for name {} has been found !", serverName);
            return;
        } else if (GeneralUtils.isEmpty(server) && applicationContext.containsBean(serverName)) {
            server = (SocketServer) applicationContext.getBean(serverName);
            log.debug("netty server instance for name {} has been found !", serverName);
        }
        if (server.start()) {
            log.info("netty server started on port {}", properties.getPort());
        } else {
            log.error("netty server start failed!");
        }
    }
}
