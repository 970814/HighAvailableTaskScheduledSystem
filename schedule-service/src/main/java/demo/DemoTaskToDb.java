package demo;

import bean.Edge;
import bean.ScheduleTask;
import bean.SubTask;
import bean.TaskDAG;
import com.alibaba.druid.util.HexBin;
import db.TaskDbUtil;
import util.Utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class DemoTaskToDb {




    public static String addDemoTask(String taskId) {
//        假设zip包已经解析完成，因此可得到如下数据，然后存入数据库
        List<String> subTaskIds = new ArrayList<>();//子任务列表
        subTaskIds.add("A");   //       A  B       使用此依赖关系为例子
        subTaskIds.add("B");   //       |/ |
        subTaskIds.add("C");   //       C  D
        subTaskIds.add("D");   //       |/
        subTaskIds.add("E");   //       E
        List<Edge> taskDeps = new ArrayList<>();//任务之间的依赖关系列表 (A, C)、(B, C)、(B, D)、(C, E)、(D, E)
        taskDeps.add(new Edge("A", "C"));
        taskDeps.add(new Edge("B", "C"));
        taskDeps.add(new Edge("B", "D"));
        taskDeps.add(new Edge("C", "E"));
        taskDeps.add(new Edge("D", "E"));

        TaskDAG taskDAG = new TaskDAG(subTaskIds, taskDeps);// 构建DAG对象

//        子任务列表
        SubTask A = new SubTask(taskId, "A", 0, 0, 0, "echo A");
        SubTask B = new SubTask(taskId, "B", 0, 0, 0, "echo B");
        SubTask C = new SubTask(taskId, "C", 0, 2, 0, "echo C");
        SubTask D = new SubTask(taskId, "D", 0, 1, 0, "echo D");
        SubTask E = new SubTask(taskId, "E", 0, 2, 0, "echo E");
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.add(A);
        subTasks.add(B);
        subTasks.add(C);
        subTasks.add(D);
        subTasks.add(E);

//      定时任务
        ScheduleTask scheduleTask = new ScheduleTask(taskId, "我的第一个定时任务", 60 * 1000L,
                taskDAG, false, 0, 0, subTasks);

        TaskDbUtil.writeTaskToDb(scheduleTask);
        return taskId;
    }

    public static void enabledScheduleTaskDemo(String taskId)  {
        TaskDbUtil.enableScheduleTask(taskId, true, 60 * 1000L, 0);
    }
    public static void disabledScheduleTaskDemo(String taskId)  {
        TaskDbUtil.enableScheduleTask(taskId, false, 60 * 1000L, 0);
    }

    public static void showEnabledScheduleTask()  {
//      每当启动一个任务时，需要把其所有的子任务状态设置为等待。
        List<ScheduleTask> scheduleTasks = TaskDbUtil.selectEnabledScheduleTask();
        for (ScheduleTask scheduleTask : scheduleTasks)
            for (SubTask subTask : scheduleTask.getSubTaskMap().values())
                System.out.println(subTask);
        System.out.println(scheduleTasks);

    }

    public static void main(String[] args) {
//        String taskId = "523003BB4B9D9E3CF2877B785E18B6E18DD6A60E98636FDDEAD75A895F049204";
        String taskId = "18FE5F9E4DB147556837C5486CEA0CF7BCC43FDFAFC48C30E2CA0F147D5CACDC";
//        String taskId = Utils.generateRandomTaskId();

//        addDemoTask(taskId);
        enabledScheduleTaskDemo(taskId);
//        disabledScheduleTaskDemo(taskId);
        showEnabledScheduleTask();
//        TaskDbUtil.finishSubTask("7971150C73710F64FD01989B7403CC84EB278E95EEA18554CD304B502473DC79", "B");
//        showEnabledScheduleTask();

    }

}

