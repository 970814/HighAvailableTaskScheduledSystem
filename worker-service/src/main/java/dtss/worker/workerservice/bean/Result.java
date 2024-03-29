package dtss.worker.workerservice.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    String txId;
    String taskPid;
    String subTaskName;
    int retryCount;
    String endDatetime;
    long costTime;
    int exitCode;
    String log;


}
