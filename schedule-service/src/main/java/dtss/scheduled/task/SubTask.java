package dtss.scheduled.task;

import dtss.scheduled.bean.ExecutionRecord;
import dtss.scheduled.bean.Req;
import dtss.scheduled.db.TaskDbUtil;
import dtss.scheduled.util.OKHttpUtil;
import dtss.tp.ThreadPool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

// 子任务
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class SubTask {
    String taskPid;       //   父任务id
    String subTaskName;    // 子任务名称，通常是.job文件的名称
    int activationValue; //激活值
    int startThreshold; //启动阈值
    int status; // 运行状态     结束0 等待1 运行2
    String command;//子任务的命令
    int retryCount;//重试次数

    public SubTask(String taskPid, String subTaskName, int activationValue, int startThreshold, int status, String command, Integer retryCount) {
        this.taskPid = taskPid;
        this.subTaskName = subTaskName;
        this.activationValue = activationValue;
        this.startThreshold = startThreshold;
        this.status = status;
        this.command = command;
        this.retryCount = retryCount;
    }

    ExecutionRecord executionRecord;

    //    异步执行子任务
    public void run(String txId) {
        try {
            run0(txId);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @SneakyThrows
    private void run0(String txId) {


        TaskDbUtil.executeTransaction(conn -> {
            //        写入子任务运行记录
            TaskDbUtil.startExecutionRecord(conn, executionRecord = ExecutionRecord.start(txId, taskPid, subTaskName, retryCount));
            TaskDbUtil.updateSubTaskStatus(conn, taskPid, subTaskName, status = 2, retryCount);//更新运行状态: 等待 -> 运行
        });


//        runOnLocal(txId,taskPid,subTaskName,retryCount);
        runOnWorkerService(txId, taskPid, subTaskName, retryCount, command);

    }

    private void runOnWorkerService(String txId, String taskPid, String subTaskName, int retryCount,String command) {
        Req req = new Req(txId, taskPid, subTaskName, retryCount, command);
        String result = OKHttpUtil.submitTaskToWorkerService(req);
        String name = "事务《"
                + txId.replaceFirst("^(...).*(...)$", "$1...$2")
                + "-" + taskPid.replaceFirst("^(...).*(...)$", "$1...$2")
                + "-" + subTaskName
                + "-" + retryCount
                + "》";

        System.out.println(name + "成功发送至工作节点 " + result);
        if (!result.contains("{\"status\":1}"))
            throw new RuntimeException("试图在工作节点上启动子任务失败: " + result);
    }

    private void runOnLocal(String txId, String taskPid, String subTaskName, int retryCount) {
        String name = "事务《"
                + txId.replaceFirst("^(...).*(...)$", "$1...$2")
                + "-" + taskPid.replaceFirst("^(...).*(...)$", "$1...$2")
                + "-" + subTaskName
                + "-" + retryCount
                + "》";
        log.info(name + "即将执行");
        ThreadPool.getThreadPool().execute(() -> {
            try {
                Thread.sleep(100);
                SecureRandom rnd = new SecureRandom();
                int ses = rnd.nextInt(1) + 1;//假设任务需要执行1～10s中
                System.out.println("------" + name + "执行中(预计需要)" + ses + "秒---------");
                Thread.sleep(ses * 1000);
                System.out.println(command);
                System.out.println("------" + name + "执行完成---------");
                Thread.sleep(100);
                //更新运行状态: 运行 -> 结束、指向的子任务激活值加一, 该事件可被轮询线程捕获
                int exitCode = rnd.nextInt(2); // 0 正常结束，否则异常结束
                TaskDbUtil.executeTransaction(conn -> {
                    TaskDbUtil.finishSubTask(conn, taskPid, subTaskName, exitCode == 0 ? 0 : -1);
                    TaskDbUtil.endExecutionRecord(conn, executionRecord.finish(exitCode));

                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


    public boolean isFinish() {
        return status == 0;
    }

}
