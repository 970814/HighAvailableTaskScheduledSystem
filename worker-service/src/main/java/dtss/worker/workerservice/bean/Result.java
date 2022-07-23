package dtss.worker.workerservice.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    String taskPid;
    String subTaskName;
    int exitCode;
    long costTm;
    String endDatetime;
}
