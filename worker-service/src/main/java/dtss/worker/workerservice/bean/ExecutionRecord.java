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
    String result;  //子任务运行结果
    int retryCount;
    String log;
    public ExecutionRecord(String txId, String taskId, String subTaskId, int retryCount,
                           String endDatetime, long costTime, int exitCode, String log) {

        this.txId = txId;
        this.taskId = taskId;
        this.subTaskId = subTaskId;
        this.retryCount = retryCount;
        this.endDatetime = endDatetime;
        this.costTime = costTime;
        this.result = exitCode == 0 ? "成功" : "异常(" + exitCode + ")";
        this.log = log;
    }
}
