package dtss.simpleui.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

// 定时任务
@Data
@NoArgsConstructor
@Slf4j
public class ScheduleTask {
    //    该任务的唯一标识，使用zip包的SHA256值，zip包将会被重命名为taskId值。因此路径不用单独定义
    String taskId;
    String name;       //任务名称，用于前端展示，可以重名，可修改
    Long period;      //执行周期
    boolean enabled; // 任务总开关
    int status;    // 运行状态 结束0 运行2   不需要等待状态 等待1
    Integer scheduledNodeId;         // 该任务执行所在的调度节点Id

    public static List<String> getColumnNames() {
        return Arrays.asList(
                "任务名称",
                "任务Id",
                "执行周期",
                "启停状态",
                "运行状态",
                "节点位置"
        );
    }

    public String[] toRow() {
        return new String[]{
                name == null ? "/" : name,
                taskId == null ? "/" : taskId.replaceFirst("^(...).*(...)$","$1...$2"),
                period == null ? "/" : String.valueOf(period),
                enabled  ? "启用" : "关闭",
                status == 2 ? "运行" : "结束",
                scheduledNodeId == null ? "/" : String.valueOf(scheduledNodeId),
        };
    }

}

