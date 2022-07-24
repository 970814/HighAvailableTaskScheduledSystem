package dtss.scheduled.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Req {
    String txId;
    String taskPid;
    String subTaskName;
    int retryCount;
    String command;


}
