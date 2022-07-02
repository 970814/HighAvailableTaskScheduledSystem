package parse;

import bean.Edge;
import bean.ScheduleTask;
import bean.SubTask;
import bean.TaskDAG;
import com.alibaba.druid.util.HexBin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.DruidUtils;
import org.apache.commons.dbutils.QueryRunner;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Demo {

    public static void main(String[] args) throws SQLException, JsonProcessingException {
//        假设zip包已经解析完成，因此可得到如下数据，然后存入数据库
        String taskId = getRndHex(32);
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
//      定时任务
        ScheduleTask scheduleTask = new ScheduleTask(taskId, 60 * 1000L,
                taskDAG, false, 0, 0);
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

        writeTaskToDb(scheduleTask, subTasks);



    }

    private static void writeTaskToDb(ScheduleTask st, List<SubTask> subTasks) throws SQLException, JsonProcessingException {
        QueryRunner queryRunner = new QueryRunner(DruidUtils.getDataSource());
        ObjectMapper objectMapper = new ObjectMapper();
        String dagStr = objectMapper.writeValueAsString(st.getTaskDAG());
        int rows = queryRunner.update("insert into schedule_task(task_id,period,task_dag,enabled,status,max_iter_cnt) values(?,?,?,?,?,?)",
                st.getTaskId(), st.getPeriod(), dagStr, st.isEnabled(), st.getStatus(), st.getMaxIterCnt());
        if (rows != 1) throw new RuntimeException("写入数据失败: rows=" + rows);
        for (SubTask t : subTasks) {
            rows = queryRunner.update("insert into sub_task(task_pid,sub_task_id,activation_value,start_threshold,status,command) values (?,?,?,?,?,?)",
                    t.getTaskPid(), t.getSubTaskId(), t.getActivationValue(), t.getStartThreshold(),  t.getStatus(), t.getCommand());
            if (rows != 1) throw new RuntimeException("写入数据失败: rows=" + rows);
        }

    }


    private static String getRndHex(int n) {
        SecureRandom secRnd = new SecureRandom();
        byte[] sha256 = new byte[n];
        secRnd.nextBytes(sha256);
        return HexBin.encode(sha256);
    }


}
