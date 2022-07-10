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

    public ExecutionRecord(String txId, String taskId, String subTaskId, long startTm, String result) {
        this.txId = txId;
        this.taskId = taskId;
        this.subTaskId = subTaskId;
        startDatetime = Utils.currentCSTDateTimeStr(startTm);
        this.result = result;
        this.startTm = startTm;
    }

    public ExecutionRecord finish(long endTm) {
        endDatetime = Utils.currentCSTDateTimeStr(endTm);
        result = "成功";
        costTime = (int) (((this.endTm = endTm) - startTm) / 1000);
        return this;
    }
}
