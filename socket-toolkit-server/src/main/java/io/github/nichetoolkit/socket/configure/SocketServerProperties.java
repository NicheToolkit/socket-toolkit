package io.github.nichetoolkit.socket.configure;

import io.github.nichetoolkit.rest.util.common.GeneralUtils;
import io.github.nichetoolkit.socket.enums.ServerType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * <p>SocketServerProperties</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
@Component
@ConfigurationProperties(prefix = "nichetoolkit.socket.server")
public class SocketServerProperties {
    /** 是否启用 */
    private Boolean enabled = true;
    /** 服务器名称 */
    private String name = null;
    /** 服务器监听端口  */
    private Integer port = 9999;
    /** 服务器类型 */
    private ServerType serverType = ServerType.NETTY;
    /** 使用协议 */
    private String protocol = "tcp";
    /** 主节点数量 */
    private Integer masterSize = 1;
    /** 从节点数量 */
    private Integer slaveSize = 10;
    /** 读超时时间(单位分钟) */
    private Integer readTimeout = 15;
    /** 缓冲队列大小 */
    private Integer backlog = 128;
    /** 是否实时性 */
    private Boolean tcpNoDelay= true;
    /** 保持长连接 */
    private Boolean keepalive = true;

    @NestedConfigurationProperty
    private ThreadPool thread = new ThreadPool();

    public SocketServerProperties() {
    }

    public static class ThreadPool {
        /** 线程池核心数量 */
        private Integer corePoolSize = 1;
        /** 线程池最大数量 */
        private Integer maxPoolSize = 10;
        /** 线程保持时间 */
        private Integer keepaliveTime = 1000;

        public ThreadPool() {
        }

        public Integer getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public Integer getKeepaliveTime() {
            return keepaliveTime;
        }

        public void setKeepaliveTime(Integer keepaliveTime) {
            this.keepaliveTime = keepaliveTime;
        }
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        if (GeneralUtils.isEmpty(this.name)) {
            return serverType.getValue();
        }
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getMasterSize() {
        return masterSize;
    }

    public void setMasterSize(Integer masterSize) {
        this.masterSize = masterSize;
    }

    public Integer getSlaveSize() {
        return slaveSize;
    }

    public void setSlaveSize(Integer slaveSize) {
        this.slaveSize = slaveSize;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getBacklog() {
        return backlog;
    }

    public void setBacklog(Integer backlog) {
        this.backlog = backlog;
    }

    public Boolean getTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(Boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public Boolean getKeepalive() {
        return keepalive;
    }

    public void setKeepalive(Boolean keepalive) {
        this.keepalive = keepalive;
    }

    public ThreadPool getThread() {
        return thread;
    }

    public void setThread(ThreadPool thread) {
        this.thread = thread;
    }
}
