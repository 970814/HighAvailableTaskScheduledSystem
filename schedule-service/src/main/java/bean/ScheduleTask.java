package bean;

import lombok.AllArgsConstructor;
import lombok.Data;

// 定时任务
@Data
@AllArgsConstructor
public class ScheduleTask {
//    该任务的唯一标识，使用zip包的SHA256值，zip包将会被重命名为taskId值。因此路径不用单独定义
    String taskId;
    Long period;      //执行周期
    TaskDAG taskDAG; // 子任务间的执行依赖关系
    boolean enabled; // 任务总开关
    int status;    // 运行状态 结束0 等待1 运行2
    //最大执行次数，当任务被启动后，执行达到maxIterCnt次后，将会自动关闭。
    //如果需要执行一次性任务，该值可设置为1
    Integer maxIterCnt;
}

