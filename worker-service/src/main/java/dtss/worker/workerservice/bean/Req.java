package dtss.worker.workerservice.bean;

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

    @SneakyThrows
    public static void main(String[] args) {
        Req req = new Req("DAF807637D2BC0318C474F66C8DE119BB072ACAEBACD9AB23825CB199E8D9DB6",
                "DAF807637D2BC0318C474F66C8DE119BB072ACAEBACD9AB23825CB199E8D9DB6",
                "main", 0, "sh execJar.sh");
        System.out.println(new ObjectMapper().writeValueAsString(req));

    }
}
