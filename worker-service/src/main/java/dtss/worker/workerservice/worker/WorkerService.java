package dtss.worker.workerservice.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerService {
    ExecutorService threadPool = Executors.newFixedThreadPool(100);

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public static void main(String[] args) {
        WorkerService workerService = new WorkerService();
        Task task = new Task(workerService, "./subJobA.sh");
        int code = task.run();

        System.out.println(code);
        System.exit(0);

    }


}
