package dtss.simpleui.background;

import dtss.simpleui.bean.ExecutionRecord;
import dtss.simpleui.bean.ScheduleTask;
import dtss.simpleui.bean.SubTask;
import dtss.simpleui.db.DruidUtil;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DBUtils {

    public static List<ExecutionRecord> selectLastestExecutionRecord(String taskId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        String sql = "select sub_task_id subTaskId, " +
                "start_datetime startDatetime, " +
                "end_datetime endDatetime, " +
                "cost_time costTime, " +
                "result result, " +
                "tx_id txId, " +
                "task_id taskId " +
                "from execution_record " +
                "where task_id = ? " +
                "and sub_task_id is null order by startDatetime desc";
//        System.out.println(sql);
        return queryRunner.query(sql, new BeanListHandler<>(ExecutionRecord.class), taskId);
    }

    public static List<ExecutionRecord> selectExecutionRecordBy(String txId, String taskId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        String sql = "select sub_task_id subTaskId, " +
                "start_datetime startDatetime, " +
                "end_datetime endDatetime, " +
                "cost_time costTime, " +
                "result result, " +
                "tx_id txId, " +
                "task_id taskId " +
                "from execution_record " +
                "where tx_id = ?  and task_id = ? " +
                "and sub_task_id is not null order by startDatetime";
//        System.out.println(sql);
        return queryRunner.query(sql, new BeanListHandler<>(ExecutionRecord.class), txId, taskId);
    }


    @SneakyThrows
    public static List<ExecutionRecord> show() {
        List<ExecutionRecord> history = selectLastestExecutionRecord("523003BB4B9D9E3CF2877B785E18B6E18DD6A60E98636FDDEAD75A895F049204");
        history.sort(Comparator.comparing(ExecutionRecord::getStartDatetime).reversed());
        ExecutionRecord taskEr = history.get(0);
        List<ExecutionRecord> records = selectExecutionRecordBy(taskEr.getTxId(), taskEr.getTaskId());
        records.add(0, taskEr);
        return new ArrayList<>(records);
    }
//    select task_pid taskPid,sub_task_id subTaskName,activation_value activationValue,start_threshold startThreshold,status,command from sub_task;
//   "scheduled_node_id scheduledNodeId " +
    @SneakyThrows
    public static List<ScheduleTask> selectTaskList()  {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        String sql = "select task_id taskId,name,period,enabled,status,scheduled_node_id scheduledNodeId from schedule_task";
        return queryRunner.query(sql, new BeanListHandler<>(ScheduleTask.class));
    }

    @SneakyThrows
    public static List<SubTask> selectSubTaskList(String taskPid) {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        String sql = "select task_pid taskPid,sub_task_id subTaskName,activation_value activationValue,start_threshold startThreshold,status,command from sub_task where task_pid = ?";
        return queryRunner.query(sql, new BeanListHandler<>(SubTask.class), taskPid);
    }

    @SneakyThrows
    public static void main(String[] args) {


        List<ScheduleTask> scheduleTasks = selectTaskList();
        for (ScheduleTask scheduleTask : scheduleTasks) System.out.println(scheduleTask);

        List<SubTask> subTasks = selectSubTaskList("523003BB4B9D9E3CF2877B785E18B6E18DD6A60E98636FDDEAD75A895F049204");
        for (SubTask subTask : subTasks) {
            System.out.println(subTask);
        }
    }

    @SneakyThrows
    public static List<ExecutionRecord> showTaskExecuteHistory(String taskId) {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        String sql = "select sub_task_id subTaskId, " +
                "start_datetime startDatetime, " +
                "end_datetime endDatetime, " +
                "cost_time costTime, " +
                "result result, " +
                "tx_id txId, " +
                "task_id taskId " +
                "from execution_record " +
                "where task_id = ? " +
                "and sub_task_id is null order by startDatetime desc";
        return queryRunner.query(sql, new BeanListHandler<>(ExecutionRecord.class), taskId);
    }

    @SneakyThrows
    public static List<ExecutionRecord> showSubTaskExecuteHistory(String txId, String taskId) {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        String sql = "select sub_task_id subTaskId, " +
                "start_datetime startDatetime, " +
                "end_datetime endDatetime, " +
                "cost_time costTime, " +
                "result result, " +
                "tx_id txId, " +
                "task_id taskId " +
                "from execution_record " +
                "where tx_id = ?  and task_id = ? " +
                "and sub_task_id is not null order by startDatetime";
//        System.out.println(sql);
        return queryRunner.query(sql, new BeanListHandler<>(ExecutionRecord.class), txId, taskId);
    }


    //    启用或关闭一个定时任务。 如果是启用，那么需要配置执行周期和最大执行次数，0表示无限制。
    @SneakyThrows
    public static void enableScheduleTask(String taskId, boolean enabled, Long period, Integer maxIterCnt,Integer scheduledNodeId) {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        int rows = queryRunner
                .update("update schedule_task set enabled = ?, period = ?, max_iter_cnt = ?, scheduled_node_id = ? where task_id = ?"
                        , enabled, period, maxIterCnt, scheduledNodeId, taskId);
        if (rows != 1) throw new RuntimeException("更新记录" + rows + "," + taskId);
    }


    public static void enabledScheduleTaskDemo(String taskId,Integer scheduledNodeId) {
        enableScheduleTask(taskId, true, 60 * 1000L, 0, scheduledNodeId);

    }

    public static void disabledScheduleTaskDemo(String taskId,Integer scheduledNodeId) {
        enableScheduleTask(taskId, false, 60 * 1000L, 0, scheduledNodeId);
    }
}
