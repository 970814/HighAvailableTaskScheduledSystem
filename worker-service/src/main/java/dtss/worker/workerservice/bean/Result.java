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
    String endDatetime;
    long costTime;
    int exitCode;


}
