package dtss.worker.workerservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import dtss.worker.workerservice.bean.ExecutionRecord;
import dtss.worker.workerservice.bean.TaskDAG;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TaskDbUtil {
    public static void endSubExecutionRecord(Connection conn, ExecutionRecord er) {
        QueryRunner queryRunner = new QueryRunner();
        int rows = 0;
        try {
            rows = queryRunner.update(conn, "update execution_record set end_datetime = ?, result = ?, cost_time = ? " +
                    "where tx_id = ? and task_id = ? and sub_task_id = ?" ,
                    er.getEndDatetime(), er.getResult(), er.getCostTime(), er.getTxId(), er.getTaskId(), er.getSubTaskId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rows != 1) throw new RuntimeException("更新数据失败: rows=" + rows);
    }

    //更新运行状态: 运行 -> 结束、指向的子任务激活值加一
    public static void finishSubTask(Connection conn, String taskPid, String subTaskName) {
//        事务（原子性）
        updateSubTaskStatus(conn, taskPid, subTaskName, 0);//设置为结束运行状态
        incrementActivationValue(conn, taskPid,
                selectTaskDAGBy(taskPid)
                        .getSubTaskNamesDrivenBy(subTaskName));//激活值+1
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
    //更新运行状态
    @SneakyThrows
    public static void updateSubTaskStatus(Connection conn, String taskPid, String subTaskName, int status) {
        QueryRunner queryRunner = new QueryRunner();
        int rows = queryRunner.update(conn,"update sub_task set status = ? " +
                "where task_pid = ? and sub_task_id = ?", status, taskPid, subTaskName);
        if (rows != 1) throw new RuntimeException("更新记录" + rows + ":" + taskPid + "." + subTaskName + "." + status);
    }

    private static String getPlaceHolder(int n) {
        StringBuilder placeholder = new StringBuilder("(");
        for (int i = 0; i < n; i++) placeholder.append("?,");
        placeholder.deleteCharAt(placeholder.length() - 1).append(')');
        return placeholder.toString();
    }
    @SneakyThrows
    @NonNull
    public static TaskDAG selectTaskDAGBy(String taskId) {
        QueryRunner queryRunner = new QueryRunner(DruidUtil.getDataSource());
        return new ObjectMapper().readValue((String)queryRunner
                        .query("select task_dag from schedule_task where task_id = ?", new ScalarHandler<>(), taskId),
                TaskDAG.class);
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
