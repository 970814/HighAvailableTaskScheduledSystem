package schedule;

import java.util.concurrent.*;

public class ScheduleServer {
//    最大同时执行的定时任务数量
    int corePoolSize = 10;
    final ScheduledExecutorService scheduledExecutorService;
    public ScheduleServer() {
        scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize);

        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println(System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, 1, 1, TimeUnit.SECONDS);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        scheduledFuture.cancel(true);

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        new ScheduleServer();
    }


}
