package io.github.nichetoolkit.socket.server;

import org.springframework.lang.NonNull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>ServerThreadFactory</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v1.0.0
 */
public class ServerThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_SEQUENCE = new AtomicInteger(1);
    private static final AtomicInteger THREAD_SEQUENCE = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String prefix;

    public ServerThreadFactory(String prefix) {
        SecurityManager securityManager = System.getSecurityManager();
        this.group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.prefix = prefix + "-" + POOL_SEQUENCE.getAndIncrement() + "-";
    }

    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        Thread thread = new Thread(group, runnable, prefix + THREAD_SEQUENCE.getAndIncrement(), 0);
        if (thread.isDaemon()){
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY){
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
