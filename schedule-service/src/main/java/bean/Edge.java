package bean;

import lombok.AllArgsConstructor;
import lombok.Data;

//DAG中一条依赖关系边，endId 任务需要在 startId任务执行完后在执行
@Data
@AllArgsConstructor
public class Edge {
    String startId;
    String endId;


}
