package dtss.worker;

import lombok.SneakyThrows;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerService {
    ExecutorService threadPool = Executors.newFixedThreadPool(100);

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public static void main(String[] args) {
        WorkerService workerService = new WorkerService();
//        workerService.runJob("./subJobA.sh");
        

    }


}
