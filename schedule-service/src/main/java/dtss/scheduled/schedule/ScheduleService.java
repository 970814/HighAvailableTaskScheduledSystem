package dtss.scheduled.schedule;

import dtss.scheduled.task.ScheduleTask;
import dtss.scheduled.task.SubTask;
import dtss.scheduled.db.TaskDbUtil;
import dtss.scheduled.util.Utils;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.*;

// 调度服务
public class ScheduleService {
//    最大同时执行的定时任务数量
    int corePoolSize = 10;
    final ScheduledExecutorService scheduledExecutorService;
    Map<String, ScheduleTask> taskMap;// <taskId，ScheduleTask>
    Map<String, ScheduledFuture<?>> scheduledFutureMap; // 用于关闭任务
    int scheduledNodeId;
    public ScheduleService(int scheduledNodeId) {
        this.scheduledNodeId = scheduledNodeId;
//        一个是轮询数据库的线程
        scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize + 1);
        taskMap = new HashMap<>();
        scheduledFutureMap = new HashMap<>();
//        启动监听数据库线程
        listenTaskStatus();
        System.out.println("调度服务" + scheduledNodeId + "成功启动...");
    }




    private void listenTaskStatus() {
        //  每隔10s查库并更新任务状态, 实现任务的启动、关闭、以及任务状态的转换
        scheduledExecutorService
                .scheduleAtFixedRate(this::run,
                        1, 10, TimeUnit.SECONDS);
    }


    public void run() {
        try {
            loadTaskStatusFromDB();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    //    从数据库中载入任务状态
    @SneakyThrows //暂不考虑sql异常
    private void loadTaskStatusFromDB() {
        Map<String, ScheduleTask> newTaskMap = new HashMap<>();
        TaskDbUtil.selectEnabledScheduleTask(scheduledNodeId).forEach(scheduleTask -> newTaskMap.put(scheduleTask.getTaskId(), scheduleTask));

        update(taskMap, newTaskMap);//将数据库中的任务状态更新到内存
    }

    //将数据库中的任务状态更新到内存，完成3件事情
    private void update(Map<String, ScheduleTask> oldTaskMap, Map<String, ScheduleTask> newTaskMap) {
        Set<String> oldTaskIds = new HashSet<>(oldTaskMap.keySet());
        Set<String> newTaskIds = new HashSet<>(newTaskMap.keySet());

//        1.内存任务集合 减去 数据库任务集合 ---> 得到关闭的任务集合
        Utils.difference(oldTaskIds, newTaskIds)   //被关闭的任务
                .forEach(disabledTaskId -> {        //逐个关闭
                    scheduledFutureMap.remove(disabledTaskId).cancel(false);//会等到执行完成再从线程池中删除&关闭任务
                    taskMap.remove(disabledTaskId);
                });

//        2.数据库任务集合 减去 内存任务集合 ---> 得到启用的任务集合
        Utils.difference(newTaskIds, oldTaskIds)        //被启用的任务
                .forEach(enabledTaskId -> startScheduleTask(newTaskMap.get(enabledTaskId))); //逐个启动

        //3.交集部分，得到任务运行状态的变化   运行 -> 结束/失败
        //将内存中的任务运行状态和数据库中状态进行对比，以驱动DAG流程的执行
        Utils.intersection(oldTaskIds, newTaskIds)            //逐个驱动
                .stream().filter(taskId -> oldTaskMap.get(taskId).isRunning())
                .forEach(taskId -> taskStateTransition(oldTaskMap.get(taskId), newTaskMap.get(taskId)));//任务状态转换，用于驱动DAG流程执行


//        日志部分
        Set<String> disabledTaskIds = Utils.difference(oldTaskIds, newTaskIds);
        if (disabledTaskIds.size() > 0)
            System.out.println("任务成功关闭 -> " + disabledTaskIds);
        Set<String> enabledTaskIds = Utils.difference(newTaskIds, oldTaskIds);
        if (enabledTaskIds.size() > 0)
            System.out.println("任务成功部署到线程池 -> " + enabledTaskIds);
        Set<String> runningTasks = Utils.intersection(oldTaskIds, newTaskIds);
        runningTasks.addAll(enabledTaskIds);
        System.out.println("当前启用的任务 -> " + runningTasks);
    }

//    任务状态转换
    private void taskStateTransition(ScheduleTask oldTask, ScheduleTask newTask) {
        for (String taskId : oldTask.getSubTaskMap().keySet()) {
            SubTask oldSubTask = oldTask.getSubTaskMap().get(taskId);
            SubTask newSubTask = newTask.getSubTaskMap().get(taskId);
            if (!oldSubTask.isFinish() && oldSubTask.getStatus() != newSubTask.getStatus()) { //只判断未完成 且 状态发生了变化 任务
                oldSubTask.setStatus(newSubTask.getStatus());//更新之
                if (newSubTask.getStatus() == 0) { //其他的状态变化无需关心，只需要关心任务  运行->结束 的变化
                    //某子任务驱动的所有子任务激活值加一
                    oldTask.getSubTasksDrivenBy(oldSubTask.getSubTaskName())
                            .forEach(subTask -> subTask.setActivationValue(subTask.getActivationValue() + 1));
                    oldTask.wakeUp();//驱动DAG流程的执行,唤醒阻塞
                }
            }
        }
    }

    //    启动定时任务
    private void startScheduleTask(ScheduleTask scheduleTask) {
        //将任务放入线程池
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService
                .scheduleAtFixedRate(scheduleTask::run, 8, scheduleTask.getPeriod(), TimeUnit.MILLISECONDS);
        scheduledFutureMap.put(scheduleTask.getTaskId(), scheduledFuture); //用于关闭任务
        taskMap.put(scheduleTask.getTaskId(), scheduleTask);
    }

}
