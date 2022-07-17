package dtss.scheduled.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//DAG中一条依赖关系边，endId 任务需要在 startId任务执行完后在执行
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edge {
    @JsonProperty(value = "s")
    String startId;
    @JsonProperty(value = "e")
    String endId;
}
