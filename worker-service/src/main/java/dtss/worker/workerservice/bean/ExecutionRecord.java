package dtss.worker.workerservice.bean;

import dtss.scheduled.util.Utils;
import dtss.worker.workerservice.util.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    public ExecutionRecord finish() {
        return finish(System.currentTimeMillis(), "成功");
    }



    public ExecutionRecord finish(long endTm, String result) {
        endDatetime = Utils.currentCSTDateTimeStr(endTm);
        this.result = result;
        costTime = (int) (((this.endTm = endTm) - startTm) / 1000);
        return this;
    }
}
