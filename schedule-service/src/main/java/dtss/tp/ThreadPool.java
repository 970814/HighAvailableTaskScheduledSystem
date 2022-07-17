package dtss.tp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static ExecutorService threadPool = Executors.newFixedThreadPool(8);

    public static ExecutorService getThreadPool() {
        return threadPool;
    }


}
