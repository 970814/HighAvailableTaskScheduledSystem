package dtss.worker.workerservice.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExecutionRecord {

    String txId;
    String taskId;
    String subTaskId;
    String endDatetime;
    long costTime; //执行花费时间(毫秒)
    int exitCode;  //子任务运行结果

    public ExecutionRecord(String txId, String taskId, String subTaskId,
                           String endDatetime, long costTime, int exitCode) {
        this.txId = txId;
        this.taskId = taskId;
        this.subTaskId = subTaskId;
        this.endDatetime = endDatetime;
        this.costTime = costTime;
        this.exitCode = exitCode;
    }
}
