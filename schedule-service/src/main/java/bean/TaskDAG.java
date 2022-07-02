package bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

//任务的有向无环图
@Data
@AllArgsConstructor
public class TaskDAG {
    List<String> subTskIds;//子任务id列表
    List<Edge> tskDeps;//任务之间的依赖关系列表
}
