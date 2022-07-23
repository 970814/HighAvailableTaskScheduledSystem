package dtss.worker.workerservice.worker;

import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.concurrent.CountDownLatch;

public class Task {
    private final WorkerService context;
    String command;
    StringBuilder taskLog;
    CountDownLatch latch;
    public Task(WorkerService context, String command) {
        this.context = context;
        this.command = command;
    }

    @SneakyThrows
    public int run() {
        taskLog = new StringBuilder();
        File workDir = new File("/Users/liumingxing/IdeaProjects/DistributeTaskScheduleSystem/workdir");
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}, null, workDir);
        collectLog(process.getInputStream(), process.getErrorStream());
        return process.waitFor();
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
                taskLog.append(line).append('\n');
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }


}
