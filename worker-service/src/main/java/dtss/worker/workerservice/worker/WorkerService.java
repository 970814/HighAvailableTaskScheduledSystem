package dtss.worker.workerservice.worker;

import dtss.worker.workerservice.bean.ExecutionRecord;
import dtss.worker.workerservice.bean.Result;
import dtss.worker.workerservice.util.TaskDbUtil;
import dtss.worker.workerservice.util.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class WorkerService implements Runnable {
    File baseWorkDir = new File("workdir");

    public File getBaseWorkDir() {
        return baseWorkDir;
    }

    ExecutorService threadPool = Executors.newFixedThreadPool(100);

    public WorkerService() {
        threadPool.execute(this);
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    List<Future<Result>> taskFutures = Collections.synchronizedList(new LinkedList<>());

    public synchronized void newTask(String txId, String taskPid, String subTaskName, int retryCount, String command) {
        taskFutures.add(threadPool.submit(new Task(this, txId, taskPid, subTaskName, retryCount, command)));
    }

    @Override
    public void run() {
        try {
            handlerTaskCompletionEvent();  //处理任务完成事件
        } catch (Throwable throwable) {
            log.error("Exception -> ", throwable);
            System.exit(1);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @SneakyThrows
    private void handlerTaskCompletionEvent() {
        while (true) {
            if (taskFutures.size() > 0) {
                synchronized (this) {
                    Iterator<Future<Result>> iter = taskFutures.iterator();
                    while (iter.hasNext()) {
                        Future<Result> future = iter.next();
                        if (future.isDone()) {
                            Result result = future.get();
                            log.info(Utils.txName(result.getTxId(), result.getTaskPid(), result.getSubTaskName(), result.getRetryCount()) + "执行完成");
                            TaskDbUtil.executeTransaction(conn -> {
                                TaskDbUtil.finishSubTask(conn, result.getTaskPid(), result.getSubTaskName(),
                                        result.getExitCode() == 0 ? 0 : -1
                                );
                                TaskDbUtil.endSubExecutionRecord(conn, new ExecutionRecord(
                                        result.getTxId(), result.getTaskPid(), result.getSubTaskName(), result.getRetryCount(),
                                        result.getEndDatetime(), result.getCostTime(), result.getExitCode(), result.getLog()
                                ));
                            });
                            iter.remove();
                        }
                    }
                }
            }
            //noinspection BusyWait
            Thread.sleep(5 * 1000);
        }
    }
}
