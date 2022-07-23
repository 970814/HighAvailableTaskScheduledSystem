package dtss.worker.workerservice.worker;

import dtss.worker.workerservice.bean.ExecutionRecord;
import dtss.worker.workerservice.bean.Result;
import dtss.worker.workerservice.util.TaskDbUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class WorkerService implements Runnable {

    ExecutorService threadPool = Executors.newFixedThreadPool(100);

    public WorkerService() {
        threadPool.execute(this);
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    List<Future<Result>> taskFutures = Collections.synchronizedList(new LinkedList<>());

    public void newTask(String txId, String taskPid, String subTaskName) {
        taskFutures.add(threadPool.submit(new Task(this, txId, taskPid, subTaskName)));
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
                Iterator<Future<Result>> iter = taskFutures.iterator();
                while (iter.hasNext()) {
                    Future<Result> future = iter.next();
                    if (future.isDone()) {
                        Result result = future.get();
                        if (result.getExitCode() == 0) {        //如果任务执行成功
                            TaskDbUtil.executeTransaction(conn -> {
                                TaskDbUtil.finishSubTask(conn, result.getTaskPid(), result.getSubTaskName());
                                TaskDbUtil.endSubExecutionRecord(conn, new ExecutionRecord(
                                        result.getTxId(), result.getTaskPid(), result.getSubTaskName(),
                                        result.getEndDatetime(), result.getCostTime(), result.getExitCode()
                                ));
                            });
                        } else {  //任务执行失败

                        }
                        iter.remove();
                    }
                }
            }
        }
    }
}
