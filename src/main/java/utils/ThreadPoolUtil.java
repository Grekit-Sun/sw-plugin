package utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Description：
 *
 * @data:2022/5/14 下午2:40
 * @author: ZhengXiang Sun
 */
public final class ThreadPoolUtil {

    /**
     * 单例，私有化构造方法并提供获得方法
     */


    private static ThreadPoolUtil mThreadPoolUtil;


    public static ThreadPoolUtil getInstance() {
        if (mThreadPoolUtil == null) {
            synchronized (ThreadPoolUtil.class) {
                if (mThreadPoolUtil == null) {
                    mThreadPoolUtil = new ThreadPoolUtil();
                }
            }
        }
        return mThreadPoolUtil;
    }

    /**
     * 核心线程池的数量，同时执行的线程数量
     */
    private int corePoolSize;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 1;

    private TimeUnit unit = TimeUnit.HOURS;

    private ThreadPoolExecutor executor;

    /**
     * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
     */
    private ThreadPoolUtil() {
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        //虽然maximumPoolSize用不到，但是需要赋值，否则报错
        maximumPoolSize = corePoolSize;
        initThreadPool();
    }

    /**
     * 初始化
     */
    private void initThreadPool() {
        executor = new ThreadPoolExecutor(
                //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                corePoolSize,
                //先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,
                // 但是它的数量是包含了corePoolSize的
                maximumPoolSize,
                //表示的是maximumPoolSize当中等待任务的存活时间
                keepAliveTime,
                unit,
                //缓冲队列，用于存放等待任务，Linked的先进先出
                new LinkedBlockingQueue<Runnable>(),
                //创建线程的工厂
                new DefaultThreadFactory(Thread.NORM_PRIORITY, "wl-pool-"),
                //用来对超出maximumPoolSize的任务的处理策略
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 执行任务
     */
    public void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (executor == null) {
            initThreadPool();
        }
        executor.execute(runnable);
    }

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从线程池中移除任务
     */
    public void remove(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (executor == null) {
            initThreadPool();
        }
        executor.remove(runnable);
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    /**
     * 创建线程的工厂，设置线程的优先级，group，以及命名
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        /**
         * 线程池的计数
         */
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

        /**
         * 线程的计数
         */
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final String namePrefix;
        private final int threadPriority;

        private DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread t = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            t.setPriority(threadPriority);
            return t;
        }
    }
}
