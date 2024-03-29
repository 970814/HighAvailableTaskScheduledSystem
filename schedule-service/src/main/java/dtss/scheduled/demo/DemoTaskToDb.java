package dtss.scheduled.demo;

import dtss.scheduled.bean.Edge;
import dtss.scheduled.task.ScheduleTask;
import dtss.scheduled.task.SubTask;
import dtss.scheduled.bean.TaskDAG;
import dtss.scheduled.db.TaskDbUtil;
import dtss.scheduled.util.Utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DemoTaskToDb {


    public static String addDemoTask(String taskId, Integer scheduledNodeId, String name) {
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
        SubTask A = new SubTask(taskId, "A", 0, 0, 0, "echo A", 0);
        SubTask B = new SubTask(taskId, "B", 0, 0, 0, "echo B", 0);
        SubTask C = new SubTask(taskId, "C", 0, 2, 0, "echo C", 0);
        SubTask D = new SubTask(taskId, "D", 0, 1, 0, "echo D", 0);
        SubTask E = new SubTask(taskId, "E", 0, 2, 0, "echo E", 0);
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.add(A);
        subTasks.add(B);
        subTasks.add(C);
        subTasks.add(D);
        subTasks.add(E);

//      定时任务
        ScheduleTask scheduleTask = new ScheduleTask(taskId, name, 60 * 1000L,
                taskDAG, false, 0, 0, 1, subTasks);

        TaskDbUtil.executeTransaction(conn -> TaskDbUtil.writeTaskToDb(conn, scheduleTask));
        return taskId;
    }

    public static void enabledScheduleTaskDemo(Connection conn, String taskId, Integer scheduledNodeId) {
        TaskDbUtil.enableScheduleTask(conn, taskId, true, 60 * 1000L, 0, scheduledNodeId);
    }
    public static void disabledScheduleTaskDemo(Connection conn,String taskId,Integer scheduledNodeId)  {
        TaskDbUtil.enableScheduleTask(conn,taskId, false, 60 * 1000L, 0, scheduledNodeId);
    }

    public static void showEnabledScheduleTask()  {
//      每当启动一个任务时，需要把其所有的子任务状态设置为等待。
        List<ScheduleTask> scheduleTasks = TaskDbUtil.selectAllScheduleTask();
        for (ScheduleTask scheduleTask : scheduleTasks) {
            System.out.println(scheduleTask);
            for (SubTask subTask : scheduleTask.getSubTaskMap().values())
                System.out.println(subTask);
        }
    }

    public static void main(String[] args) {
//        String taskId = "523003BB4B9D9E3CF2877B785E18B6E18DD6A60E98636FDDEAD75A895F049204";

//        String taskId = "18FE5F9E4DB147556837C5486CEA0CF7BCC43FDFAFC48C30E2CA0F147D5CACDC";

//        String taskId = Utils.generateRandomTaskId();
//        System.out.println(taskId);

//        addDemoTask( "523003BB4B9D9E3CF2877B785E18B6E18DD6A60E98636FDDEAD75A895F049204", 1, "我的第一个定时任务");
//        addDemoTask("18FE5F9E4DB147556837C5486CEA0CF7BCC43FDFAFC48C30E2CA0F147D5CACDC", 2, "第二个定时任务");
//        enabledScheduleTaskDemo(taskId,1);
//        enabledScheduleTaskDemo(taskId);
//        disabledScheduleTaskDemo(taskId);
//        showEnabledScheduleTask();

//        TaskDbUtil.finishSubTask("7971150C73710F64FD01989B7403CC84EB278E95EEA18554CD304B502473DC79", "B");
//        showEnabledScheduleTask();


        addDemo2Task();
    }

    private static void addDemo2Task() {
        String taskId = "DAF807637D2BC0318C474F66C8DE119BB072ACAEBACD9AB23825CB199E8D9DB6";
        String name = "定时测试Jar包任务";
        List<String> subTaskIds = new ArrayList<>();//子任务列表
        subTaskIds.add("main");
        subTaskIds.add("end");
        List<Edge> taskDeps = new ArrayList<>();//任务之间的依赖关系列表 (main, end)
        taskDeps.add(new Edge("main", "end"));
        TaskDAG taskDAG = new TaskDAG(subTaskIds, taskDeps);// 构建DAG对象

//        子任务列表
        SubTask main = new SubTask(taskId, "main", 0, 0, 0, "sh execJar.sh", 0);
        SubTask end = new SubTask(taskId, "end", 0, 1, 0, "echo 结束", 0);
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.add(main);
        subTasks.add(end);

//        定时任务
        ScheduleTask scheduleTask = new ScheduleTask(taskId, name, 60 * 1000L,
                taskDAG, false, 0, 0, null, subTasks);

        TaskDbUtil.executeTransaction(conn -> TaskDbUtil.writeTaskToDb(conn, scheduleTask));
    }

}

