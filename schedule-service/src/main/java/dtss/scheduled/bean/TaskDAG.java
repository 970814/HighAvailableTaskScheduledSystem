package dtss.scheduled.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//任务的有向无环图
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDAG {
    List<String> subTaskNames;//子任务id列表
    List<Edge> taskDeps;//任务之间的依赖关系列表

    //    得到某子任务驱动的子任务列表
    public List<String> getSubTaskNamesDrivenBy(String taskName) {
        if (!subTaskNames.contains(taskName)) throw new RuntimeException();
        List<String> subTaskNames = new ArrayList<>();
        taskDeps.stream()
                .filter(edge -> taskName.equals(edge.startId))
                .forEach(edge -> subTaskNames.add(edge.endId));
        return subTaskNames;
    }
}
