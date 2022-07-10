package bean;

import db.TaskDbUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.Utils;

import java.util.Random;

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

    public SubTask(String taskPid, String subTaskName, int activationValue, int startThreshold, int status, String command) {
        this.taskPid = taskPid;
        this.subTaskName = subTaskName;
        this.activationValue = activationValue;
        this.startThreshold = startThreshold;
        this.status = status;
        this.command = command;
    }

        ExecutionRecord executionRecord;
    //    异步执行子任务
    public void run(String txId) {
//        写入运行记录
        TaskDbUtil.startExecutionRecord(executionRecord = new ExecutionRecord(txId, taskPid, subTaskName, System.currentTimeMillis(), "运行"));

//        这里需要将子任务传输到 任务执行节点 进行执行
//        先进行简单模拟

        String name = "子任务《" + taskPid.replaceFirst("^(...).*(...)$","$1...$2") + "-" + subTaskName + "》";
        TaskDbUtil.updateSubTaskStatus(taskPid, subTaskName, status = 2);//更新运行状态: 等待 -> 运行
        System.out.println(name + "成功发生到执行节点");
        new Thread(() -> {
            try {
                Thread.sleep(100);
                Random rnd = new Random();
                int ses = rnd.nextInt(10)  + 1;//假设任务需要执行1～10s中
                System.out.println("------" + name + "执行中(预计需要)"+ses+"秒---------");
                Thread.sleep(ses * 1000);
                System.out.println(command);
                System.out.println("------" + name + "执行完成---------");
                Thread.sleep(100);
                //更新运行状态: 运行 -> 结束、指向的子任务激活值加一, 该事件可被轮询线程捕获
                TaskDbUtil.finishSubTask(taskPid, subTaskName);
                TaskDbUtil.endExecutionRecord(executionRecord.finish(System.currentTimeMillis()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


    }

    public boolean isFinish() {
        return status == 0;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "subTaskName='" + subTaskName + '\'' +
                ", activationValue=" + activationValue +
                ", startThreshold=" + startThreshold +
                ", status=" + status +
                '}';
    }
}
