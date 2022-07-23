package dtss.worker;

import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class Task {
    private WorkerService context;
    String command;

    StringBuilder taskLog = new StringBuilder();


    public Task(WorkerService context, String command) {
        this.context = context;
        this.command = command;
    }

    public String runJob(String command) {

        try {
            File workDir = new File("/Users/liumingxing/IdeaProjects/DistributeTaskScheduleSystem/workdir");
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}, null, workDir);

            collectLog(process.getInputStream(), process.getErrorStream());


            int exitCode = process.waitFor();
            System.out.println("res: " + exitCode);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs.toString();

    }

    private void collectLog(InputStream stdout, InputStream stderr) {
        context.getThreadPool().submit(() -> collectLogFrom(stdout));
        context.getThreadPool().submit(() -> collectLogFrom(stderr));
    }

    @SneakyThrows
    private void collectLogFrom(InputStream in) {
        LineNumberReader input = new LineNumberReader(new InputStreamReader(in));
        String line;
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }

    }
}
