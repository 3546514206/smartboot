package org.smartboot.flow.core.parser;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qinluo
 * @date 2023/3/19 14:02
 * @since 1.0.8
 */
public class ThreadPoolCreator {

    public static ThreadPoolExecutor create(List<ElementAttr> attrs) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());

        boolean prestartAllCoreThreads = false;

        for (ElementAttr attr : attrs) {
            String name = attr.getName().substring(ParseConstants.THREAD.length());
            switch (name) {
                case "corePoolSize":
                    executor.setCorePoolSize(Integer.parseInt(attr.getValue()));
                    break;
                case "maximumPoolSize":
                    executor.setMaximumPoolSize(Integer.parseInt(attr.getValue()));
                    break;
                case "keepAliveTime":
                    executor.setKeepAliveTime(Long.parseLong(attr.getValue()), TimeUnit.MILLISECONDS);
                    break;
                case "handler":
                    executor.setRejectedExecutionHandler(getHandler(attr.getValue()));
                    break;
                case "threadFactory":
                    executor.setThreadFactory(new NamedThreadFactory(attr.getValue()));
                    break;
                case "prestartAllCoreThreads":
                    prestartAllCoreThreads = Boolean.parseBoolean(attr.getValue());
                    break;
            }
        }

        if (prestartAllCoreThreads) {
            executor.prestartAllCoreThreads();
        }

        return executor;
    }

    private static RejectedExecutionHandler getHandler(String value) {
        if (Objects.equals(value, ThreadPoolExecutor.AbortPolicy.class.getSimpleName())) {
            return new ThreadPoolExecutor.AbortPolicy();
        } else if (Objects.equals(value, ThreadPoolExecutor.CallerRunsPolicy.class.getSimpleName())) {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        } else if (Objects.equals(value, ThreadPoolExecutor.DiscardOldestPolicy.class.getSimpleName())) {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        } else if (Objects.equals(value, ThreadPoolExecutor.DiscardPolicy.class.getSimpleName())) {
            return new ThreadPoolExecutor.DiscardPolicy();
        }

        return new ThreadPoolExecutor.AbortPolicy();
    }

    private static class NamedThreadFactory implements ThreadFactory {

        private final String prefix;
        private final AtomicInteger sequence = new AtomicInteger(0);

        NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            if (r instanceof Thread) {
                ((Thread) r).setName(prefix + "-" + sequence.incrementAndGet());
                return (Thread) r;
            }

            Thread t = new Thread(r);
            t.setName(prefix + "-" + sequence.incrementAndGet());
            return t;
        }
    }
}
