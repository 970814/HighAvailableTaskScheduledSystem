package db;

import bean.ScheduleTask;
import bean.SubTask;
import bean.TaskDAG;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TaskDbUtil {
//    将定时任务写入数据库
    public static void writeTaskToDb(ScheduleTask st) throws SQLException, JsonProcessingException {
        Collection<SubTask> subTasks = st.getSubTaskMap().values();
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        ObjectMapper objectMapper = new ObjectMapper();
        String dagStr = objectMapper.writeValueAsString(st.getTaskDAG());
        int rows = queryRunner.update("insert into schedule_task(task_id,name,period,task_dag,enabled,status,max_iter_cnt) values(?,?,?,?,?,?,?)",
                st.getTaskId(), st.getName(), st.getPeriod(), dagStr, st.isEnabled(), st.getStatus(), st.getMaxIterCnt());
        if (rows != 1) throw new RuntimeException("写入数据失败: rows=" + rows);
        for (SubTask t : subTasks) {
            rows = queryRunner.update("insert into sub_task(task_pid,sub_task_id,activation_value,start_threshold,status,command) values (?,?,?,?,?,?)",
                    t.getTaskPid(), t.getSubTaskName(), t.getActivationValue(), t.getStartThreshold(),  t.getStatus(), t.getCommand());
            if (rows != 1) throw new RuntimeException("写入数据失败: rows=" + rows);
        }
    }

    //    查询出属于启用状态的定时任务
    public static List<ScheduleTask> selectEnabledScheduleTask() throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        List<Map<String, Object>> mapList = queryRunner.query("select task_id,name,period,task_dag,enabled,status,max_iter_cnt from schedule_task " +
                        "where enabled = true",
                new MapListHandler());
        List<ScheduleTask> scheduleTasks = new ArrayList<>();
        ObjectMapper om = new ObjectMapper();
        for (Map<String, Object> map : mapList) {
            String task_id = (String) map.get("task_id");
            String name = (String) map.get("name");
            Long period = Long.valueOf((String) map.get("period"));
            String task_dag = (String) map.get("task_dag");
            boolean enabled = (Boolean) map.get("enabled");
            int status = (Integer) map.get("status");
            Integer max_iter_cnt = (Integer) map.get("max_iter_cnt");
            scheduleTasks.add(new ScheduleTask(task_id, name, period,
                    om.readValue(task_dag, TaskDAG.class),
                    enabled, status, max_iter_cnt,
                    selectSubTasks(task_id)));
        }
        return scheduleTasks;
    }

    private static List<SubTask> selectSubTasks(String taskPid) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        List<Map<String, Object>> mapList = queryRunner
                .query("select task_pid,sub_task_id,activation_value,start_threshold,status,command from sub_task where task_pid ='"
                                + taskPid + "'",
                        new MapListHandler());
        List<SubTask> subTasks = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            String sub_task_id = (String) map.get("sub_task_id");
            int activation_value = (int) map.get("activation_value");
            int start_threshold = (int) map.get("start_threshold");
            int status = (int) map.get("status");
            String command = (String) map.get("command");
            subTasks.add(new SubTask(taskPid, sub_task_id, activation_value, start_threshold, status, command));
        }
        return subTasks;
    }


    //    启用或关闭一个定时任务。 如果是启用，那么需要配置执行周期和最大执行次数，0表示无限制。
    public static void enableScheduleTask(String taskId, boolean enabled, Long period, Integer maxIterCnt) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        int rows = queryRunner
                .update("update schedule_task set enabled = ?, period = ?, max_iter_cnt = ? where task_id = ?"
                        , enabled, period, maxIterCnt, taskId);
        System.out.println("更新任务数量：" + rows);
    }

    public static void main(String[] args) throws SQLException, IOException {

        enableScheduleTask("133A9BA04B90DDE5F8B4E67A26E527DD83A0B09795D20C9FC96AC4F80FE115D2", true, 60 * 1000L, 0);
//      每当启动一个任务时，需要把其所有的子任务状态设置为等待。


        List<ScheduleTask> scheduleTasks = selectEnabledScheduleTask();
        for (ScheduleTask scheduleTask : scheduleTasks)
            for (SubTask subTask : scheduleTask.getSubTaskMap().values())
                System.out.println(subTask);
        System.out.println(scheduleTasks);

    }

    public static void updateTaskStatus(ScheduleTask scheduleTask) {

    }



    //更新运行状态: 运行 -> 结束、指向的子任务激活值加一
    public static void finishSubTask(String taskPid, String subTaskName) {

    }

    //更新运行状态
    public static void updateSubTaskStatus(String taskPid, String subTaskName, int status) {
    }


    public static void updateTaskToStartState(ScheduleTask scheduleTask) {
    }

    public static void updateTaskToFinishState(ScheduleTask scheduleTask) {
    }
}
