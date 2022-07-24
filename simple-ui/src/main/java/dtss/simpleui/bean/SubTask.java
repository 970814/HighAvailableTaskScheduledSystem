package dtss.simpleui.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

// 子任务
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubTask {
    String taskPid;       //   父任务id
    String subTaskName;    // 子任务名称，通常是.job文件的名称
    int activationValue; //激活值
    int startThreshold; //启动阈值
    int status; // 运行状态     结束0 等待1 运行2
    String command;//子任务的命令
    Integer retryCount;

    public static List<String> getColumnNames() {
        return Arrays.asList(
                "子任务名称",
                "激活值",
                "启动阈值",
                "运行状态",
                "重试次数",
                "执行命令"
        );
    }

    public String[] toRow() {
        return new String[]{
                subTaskName == null ? "/" : subTaskName,
                String.valueOf(activationValue),
                String.valueOf(startThreshold),
                status == 2 ? "运行" : status == 1 ? "等待" : "结束",
                String.valueOf(retryCount),
                command == null ? "/" : command,
        };
    }
}
