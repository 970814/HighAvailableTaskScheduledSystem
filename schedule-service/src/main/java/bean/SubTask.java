package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 子任务
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubTask {
    String taskPid;       //   父任务id
    String subTaskName;    // 子任务id，通常是.job文件的名称
    int activationValue; //激活值
    int startThreshold; //启动阈值
    int status; // 运行状态     结束0 等待1 运行2
    String command;//子任务的命令


//    执行子任务
    public void run() {
//        这里需要将子任务传输到 任务执行节点 进行执行
//        先进行简单模拟
        System.out.println(command);

    }
}
