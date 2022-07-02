package bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

//DAG中一条依赖关系边，endId 任务需要在 startId任务执行完后在执行
@Data
@AllArgsConstructor
public class Edge {
    @JsonAlias(value = "s")
    String startId;
    @JsonAlias(value = "e")
    String endId;
}
