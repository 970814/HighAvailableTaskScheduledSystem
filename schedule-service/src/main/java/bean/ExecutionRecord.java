package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.Utils;

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

    public static ExecutionRecord start(String txId, String taskId, String subTaskId) {
        return new ExecutionRecord(txId, taskId, subTaskId, "运行");
    }

    public ExecutionRecord finish() {
        return finish(System.currentTimeMillis(), "成功");
    }

    public ExecutionRecord(String txId, String taskId, String subTaskId, String result) {
        this(txId, taskId, subTaskId, System.currentTimeMillis(), result);
    }

    public ExecutionRecord(String txId, String taskId, String subTaskId, long startTm, String result) {
        this.txId = txId;
        this.taskId = taskId;
        this.subTaskId = subTaskId;
        startDatetime = Utils.currentCSTDateTimeStr(startTm);
        this.result = result;
        this.startTm = startTm;
    }


    public ExecutionRecord finish(long endTm, String result) {
        endDatetime = Utils.currentCSTDateTimeStr(endTm);
        this.result = result;
        costTime = (int) (((this.endTm = endTm) - startTm) / 1000);
        return this;
    }
}
