package bean;

import db.TaskDbUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

// 定时任务
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTask {
//    该任务的唯一标识，使用zip包的SHA256值，zip包将会被重命名为taskId值。因此路径不用单独定义
    String taskId;
    Long period;      //执行周期
    TaskDAG taskDAG; // 子任务间的执行依赖关系
    boolean enabled; // 任务总开关
    int status;    // 运行状态 结束0 运行2   不需要等待状态 等待1
    //最大执行次数，当任务被启动后，执行达到maxIterCnt次后，将会自动关闭。
    //如果需要执行一次性任务，该值可设置为1
    Integer maxIterCnt;
//    String scheduleNodeId; // 该任务执行所在的指定调度节点Id

    List<SubTask> subTasks; // 子任务对象列表

//    启动定时任务
    public void start() {
        status = 2; // 设置为运行状态
        for (SubTask subTask : subTasks) {
            subTask.status = 1; // 子任务设置为等待状态
            subTask.activationValue = 0;
        }
        TaskDbUtil.updateTaskStatus(this);//定时任务状态设置为运行，同步到数据库
    }

    //    执行DAG定时任务
    public void run()  {
        start();
        while (subTasks.stream().anyMatch(subTask -> subTask.getStatus() != 0)) { // 只要还有任何一个子任务未完成
            List<SubTask> readyTask = subTasks.stream()
                    .filter(subTask -> subTask.getStatus() == 1) // 检索出状态为等待
                    .filter(subTask -> subTask.getActivationValue() == subTask.getStartThreshold()) //且激活值等与启动阈值
                    .collect(Collectors.toList()); // 的子任务
            ExecutorService executorService = Executors.newFixedThreadPool(subTasks.size());// 任务执行资源
            CountDownLatch latch = new CountDownLatch(readyTask.size()); //需要等待两个任务执行完成才可解除阻塞
            for (SubTask subTask : readyTask)
                subTask.run();

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        finish();
    }

//    定时任务执行结束
    private void finish() {
        status = 0;
        TaskDbUtil.updateTaskStatus(this);//定时任务状态设置为运行，同步到数据库
    }
}

