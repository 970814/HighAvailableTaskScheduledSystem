package db;

import bean.ExecutionRecord;
import bean.ScheduleTask;
import bean.SubTask;
import bean.TaskDAG;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("DuplicatedCode")
public class TaskDbUtil {
//    将定时任务写入数据库
    @SneakyThrows
    public static void writeTaskToDb(ScheduleTask st) {
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
    @SneakyThrows
    public static List<ScheduleTask> selectEnabledScheduleTask() {
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

    @SneakyThrows
    private static List<SubTask> selectSubTasks(String taskPid) {
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
    @SneakyThrows
    public static void enableScheduleTask(String taskId, boolean enabled, Long period, Integer maxIterCnt) {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        int rows = queryRunner
                .update("update schedule_task set enabled = ?, period = ?, max_iter_cnt = ? where task_id = ?"
                        , enabled, period, maxIterCnt, taskId);
        if (rows != 1) throw new RuntimeException("更新记录" + rows + "," + taskId);
    }


    @SneakyThrows
    @NonNull
    public static TaskDAG selectTaskDAGBy(String taskId) {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        return new ObjectMapper().readValue((String)queryRunner
                .query("select task_dag from schedule_task where task_id = ?", new ScalarHandler<>(), taskId),
                TaskDAG.class);
    }


    //    执行的所有子任务激活值加一
    @SneakyThrows
    private static void incrementActivationValue(Connection conn, String taskPid, List<String> subTaskNames) {
        if (subTaskNames.size() == 0) return;
        QueryRunner queryRunner = new QueryRunner();
        String placeholder = getPlaceHolder(subTaskNames.size());
        Object[] params = new Object[subTaskNames.size() + 1];
        params[0] = taskPid;
        for (int i = 0; i < subTaskNames.size(); i++) params[i + 1] = subTaskNames.get(i);
        int rows = queryRunner.update(conn, "update sub_task set activation_value = activation_value + 1 " +
                "where task_pid = ? and sub_task_id in " + placeholder, params);
        if (rows != subTaskNames.size())
            throw new RuntimeException("增加激活阈值失败：" + subTaskNames);
    }

    //更新运行状态: 运行 -> 结束、指向的子任务激活值加一
    public static void finishSubTask(Connection conn, String taskPid, String subTaskName) {
//        事务（原子性）
        updateSubTaskStatus(conn, taskPid, subTaskName, 0);//设置为结束运行状态
        incrementActivationValue(conn, taskPid,
                selectTaskDAGBy(taskPid)
                        .getSubTaskNamesDrivenBy(subTaskName));//激活值+1
    }

    //更新运行状态
    @SneakyThrows
    public static void updateSubTaskStatus(Connection conn, String taskPid, String subTaskName, int status) {
        QueryRunner queryRunner = new QueryRunner();
        int rows = queryRunner.update(conn,"update sub_task set status = ? " +
                "where task_pid = ? and sub_task_id = ?", status, taskPid, subTaskName);
        if (rows != 1) throw new RuntimeException("更新记录" + rows + ":" + taskPid + "." + subTaskName + "." + status);
    }


    //  所有子任务，重置换激活值，运行状态设置为等待
    @SneakyThrows
    private static void resetActivationValueAndSetWaitStatus(Connection conn,String taskPid, List<String> subTaskNames) {
        if (subTaskNames.size() == 0) return;
        QueryRunner queryRunner = new QueryRunner();
        String placeholder = getPlaceHolder(subTaskNames.size());
        Object[] params = new Object[subTaskNames.size() + 1];
        params[0] = taskPid;
        for (int i = 0; i < subTaskNames.size(); i++) params[i + 1] = subTaskNames.get(i);
        int rows = queryRunner.update(conn, "update sub_task set activation_value = 0, status = 1 " +
                "where task_pid = ? and sub_task_id in " + placeholder, params);
        if (rows != subTaskNames.size())
            throw new RuntimeException("增加激活阈值失败：" + subTaskNames);
    }

    private static String getPlaceHolder(int n) {
        StringBuilder placeholder = new StringBuilder("(");
        for (int i = 0; i < n; i++) placeholder.append("?,");
        placeholder.deleteCharAt(placeholder.length() - 1).append(')');
        return placeholder.toString();
    }

    public static void updateTaskToStartState(Connection conn, ScheduleTask scheduleTask) {
//        事务（原子性）
        updateTaskStatus(conn, scheduleTask.getTaskId(), 2);
        resetActivationValueAndSetWaitStatus(conn,scheduleTask.getTaskId(), scheduleTask.getTaskDAG().getSubTaskNames());
    }


    @SneakyThrows
    public static void updateTaskStatus(Connection conn,String taskId, int status) { //改造成事务的一部分
        QueryRunner queryRunner = new QueryRunner();
        int rows = queryRunner.update(conn, "update schedule_task set status = ? " +
                "where task_id = ?", status, taskId);
        if (rows != 1) throw new RuntimeException("更新记录" + rows + "," + taskId);
    }


    //    写入执行记录
    @SneakyThrows
    public static void startExecutionRecord(Connection txConn, ExecutionRecord er) {
        QueryRunner queryRunner = new QueryRunner();
        int rows = queryRunner.update(txConn, "insert into execution_record(tx_id,task_id,sub_task_id,start_datetime,end_datetime,result)" +
                        " values(?,?,?,?,?,?)",
                er.getTxId(), er.getTaskId(), er.getSubTaskId(), er.getStartDatetime(), er.getEndDatetime(),
                er.getResult());
        if (rows != 1) throw new RuntimeException("写入数据失败: rows=" + rows);
    }

    public static void endExecutionRecord(Connection conn, ExecutionRecord er) {
        QueryRunner queryRunner = new QueryRunner();

        Object[] params = new Object[]{er.getEndDatetime(), er.getResult(), er.getCostTime(), er.getTxId(), er.getTaskId(), er.getSubTaskId()};

        String endSql = "= ?";//子任务判断
        if (er.getSubTaskId() == null) {
            endSql = "is null";//主任务id判断
            params = Arrays.copyOfRange(params, 0, params.length - 1);
        }
        int rows = 0;
        try {
            rows = queryRunner.update(conn, "update execution_record set end_datetime = ?, result = ?, cost_time = ? " +
                    "where tx_id = ? and task_id = ? and sub_task_id " + endSql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rows != 1) throw new RuntimeException("更新数据失败: rows=" + rows);
    }

    @SneakyThrows
    public static void executeTransaction(Consumer<Connection> tx) {
        Connection conn = DruidUtil.getConnection();
        conn.setAutoCommit(false);//关闭自动提交
        tx.accept(conn);
        conn.commit();
        DruidUtil.close(conn);
    }

}
/*

 */