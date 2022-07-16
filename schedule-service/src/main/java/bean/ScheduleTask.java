package bean;

import db.DruidUtil;
import db.TaskDbUtil;
import javafx.concurrent.Task;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import util.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

// 定时任务
@Data
@NoArgsConstructor
@Slf4j
public class ScheduleTask {
    //    该任务的唯一标识，使用zip包的SHA256值，zip包将会被重命名为taskId值。因此路径不用单独定义
    String taskId;
    String name;       //任务名称，用于前端展示，可以重名，可修改
    Long period;      //执行周期
    TaskDAG taskDAG; // 子任务间的执行依赖关系
    boolean enabled; // 任务总开关
    int status;    // 运行状态 结束0 运行2   不需要等待状态 等待1
    //最大执行次数，当任务被启动后，执行达到maxIterCnt次后，将会自动关闭。
    //如果需要执行一次性任务，该值可设置为1
    Integer maxIterCnt;
//    String scheduledNodeId; // 该任务执行所在的调度节点Id

    Map<String, SubTask> subTaskMap; // 子任务对象列表 <name,task>

    public ScheduleTask(String taskId, String name, Long period, TaskDAG taskDAG, boolean enabled, int status, Integer maxIterCnt, List<SubTask> subTasks) {
        this.taskId = taskId;
        this.name = name;
        this.period = period;
        this.taskDAG = taskDAG;
        this.enabled = enabled;
        this.status = status;
        this.maxIterCnt = maxIterCnt;
        subTaskMap = new HashMap<>();
        subTasks.forEach(subTask -> subTaskMap.put(subTask.getSubTaskName(), subTask));
    }


    private ExecutionRecord executionRecord;


    //    (无锁)启动定时任务
    public void start() {

        TaskDbUtil.executeTransaction(conn -> {
            //        写入运行记录
            TaskDbUtil.startExecutionRecord(conn, executionRecord = ExecutionRecord.start(Utils.generateRandomTransactionId(), taskId, null));
            TaskDbUtil.updateTaskToStartState(conn, this);
        });

        //需要先更新数据库状态，再更新内存状态
        for (var subTask : subTaskMap.values()) {
            subTask.status = 1; // 子任务设置为等待状态
            subTask.activationValue = 0; //重置激活值
        }
        status = 2; // 设置为运行状态
    }

    CountDownLatch latch;


    @SneakyThrows
    public void run0() {
        start();
        log.info("---------" + name + "(" + taskId.replaceFirst("^(...).*(...)$", "$1...$2") + ")开始执行------------------------");
        var subTasks = subTaskMap.values();
        while (subTasks.stream().anyMatch(subTask -> subTask.getStatus() != 0)) { // 只要还有任何一个子任务未完成
            latch = new CountDownLatch(1);
            subTasks.stream()
                    .filter(subTask -> subTask.getStatus() == 1)                                    // 检索出状态为等待
                    .filter(subTask -> subTask.getActivationValue() == subTask.getStartThreshold()) //且激活值等与启动阈值
                    .collect(Collectors.toList())                                                   // 的就绪子任务
                    .forEach(subTask -> subTask.run(executionRecord.txId));                         //异步执行所有子任务
            latch.await();                                                                          //阻塞,等待任务状态变化驱动 (暂不处理中断异常)
        }
        finish(); //至此，所有子任务执行完成
        log.info("---------" + name + "(" + taskId.replaceFirst("^(...).*(...)$", "$1...$2") + ")执行完成------------------------");
    }

    //    定时任务执行结束
    private void finish() {

        TaskDbUtil.executeTransaction(conn -> {
            TaskDbUtil.endExecutionRecord(conn, executionRecord.finish());
            TaskDbUtil.updateTaskStatus(conn, getTaskId(), status = 0); //定时任务状态设置为结束
        });

    }

    //    得到某子任务驱动的子任务列表
    public List<SubTask> getSubTasksDrivenBy(String subTaskName) {
        return getTaskDAG()
                .getSubTaskNamesDrivenBy(subTaskName)
                .stream()
                .map(taskName -> subTaskMap.get(taskName))
                .collect(Collectors.toList());
    }

    public boolean isRunning() {
        return status == 2;
    }

    //驱动DAG流程的执行,唤醒阻塞
    public void wakeUp() {
        getLatch().countDown();
    }

    @Override
    public String toString() {
        return "ScheduleTask{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                ", status=" + status +
                '}';
    }



    //    执行DAG定时任务
    public void run() {
        try {
            run0();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

