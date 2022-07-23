package dtss.worker.workerservice.worker;

import dtss.worker.workerservice.bean.Result;
import dtss.worker.workerservice.util.Utils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class Task implements Callable<Result> {
    String txId;
    String taskPid;
    String subTaskName;
    WorkerService context;
    String command;
    StringBuilder logCollector;
    CountDownLatch latch;

    public Task(WorkerService context, String txId, String taskPid, String subTaskName) {
        this.context = context;
        this.txId = txId;
        this.taskPid = taskPid;
        this.subTaskName = subTaskName;
    }

    @Override
    @SneakyThrows
    public Result call() {
        long t0 = System.nanoTime();
        logCollector = new StringBuilder();
        File workDir = new File("/Users/liumingxing/IdeaProjects/DistributeTaskScheduleSystem/workdir");
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}, null, workDir);
        collectLog(process.getInputStream(), process.getErrorStream());
        return new Result(
                taskPid, subTaskName,                   //任务Id
                process.waitFor(),                      //任务返回值
                (System.nanoTime() - t0) / 1000_1000,   //耗时(毫秒)
                Utils.currentCSTDateTimeStr()
        );
    }

    private void collectLog(InputStream stdout, InputStream stderr) throws InterruptedException {
        latch = new CountDownLatch(2);
        context.getThreadPool().submit(() -> collectLogFrom(stdout));
        context.getThreadPool().submit(() -> collectLogFrom(stderr));
        latch.await();
    }

    private void collectLogFrom(InputStream in) {
        try {
            LineNumberReader input = new LineNumberReader(new InputStreamReader(in));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                logCollector.append(line).append('\n');
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }



}
