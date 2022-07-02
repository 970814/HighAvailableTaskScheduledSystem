package demo;

import bean.Edge;
import bean.ScheduleTask;
import bean.TaskDAG;
import com.alibaba.druid.util.HexBin;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
//        假设zip包已经解析完成，因此可得到如下数据，然后存入数据库
        String taskId = getRndHex(32);
        List<String> subTaskIds = new ArrayList<>();//子任务列表
        subTaskIds.add("A");   //       A  B       使用此依赖关系为例子
        subTaskIds.add("B");   //       |/ |
        subTaskIds.add("C");   //       C  D
        subTaskIds.add("D");   //       |/ |
        subTaskIds.add("E");   //       E
        List<Edge> taskDeps = new ArrayList<>();//任务之间的依赖关系列表 (A, C)、(B, C)、(B, D)、(C, E)、(D, E)
        taskDeps.add(new Edge("A", "C"));
        taskDeps.add(new Edge("B", "C"));
        taskDeps.add(new Edge("B", "D"));
        taskDeps.add(new Edge("C", "E"));
        taskDeps.add(new Edge("D", "E"));

        TaskDAG taskDAG = new TaskDAG(subTaskIds, taskDeps);// 构建DAG对象

        ScheduleTask scheduleTask = new ScheduleTask(taskId, 60 * 1000L,
                taskDAG, false, 0, 0);



    }


    private static String getRndHex(int n) {
        SecureRandom secRnd = new SecureRandom();
        byte[] sha256 = new byte[n];
        secRnd.nextBytes(sha256);
        return HexBin.encode(sha256);
    }


}
