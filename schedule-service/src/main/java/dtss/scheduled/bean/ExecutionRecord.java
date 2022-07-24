package dtss.scheduled.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import dtss.scheduled.util.Utils;

@Data
@NoArgsConstructor
public class ExecutionRecord {
    String txId;
    String taskId;
    String subTaskId;
    String startDatetime;
    String endDatetime;
    Integer costTime; //执行花费时间(秒)
    String result;

    long startTm;
    long endTm;
    int retryCount;

    public static ExecutionRecord start(String txId, String taskId, String subTaskId, int retryCount) {
        return new ExecutionRecord(txId, taskId, subTaskId, "运行", retryCount);
    }


    public ExecutionRecord finish(int exitCode) {
        return finish(System.currentTimeMillis(), exitCode == 0 ? "成功" : "失败");
    }

    public ExecutionRecord(String txId, String taskId, String subTaskId, String result, int retryCount) {
        this(txId, taskId, subTaskId, System.currentTimeMillis(), result, retryCount);
    }

    public ExecutionRecord(String txId, String taskId, String subTaskId, long startTm, String result, int retryCount) {
        this.txId = txId;
        this.taskId = taskId;
        this.subTaskId = subTaskId;
        startDatetime = Utils.currentCSTDateTimeStr(startTm);
        this.result = result;
        this.startTm = startTm;
        this.retryCount = retryCount;
    }


    public ExecutionRecord finish(long endTm, String result) {
        endDatetime = Utils.currentCSTDateTimeStr(endTm);
        this.result = result;
        costTime = (int) (((this.endTm = endTm) - startTm) / 1000);
        return this;
    }
}
