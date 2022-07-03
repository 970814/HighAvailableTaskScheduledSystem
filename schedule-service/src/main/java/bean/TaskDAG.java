package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//任务的有向无环图
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDAG {
    List<String> subTaskIds;//子任务id列表
    List<Edge> taskDeps;//任务之间的依赖关系列表
}
