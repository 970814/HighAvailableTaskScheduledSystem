package dtss.worker.workerservice.controller;

import dtss.worker.workerservice.worker.WorkerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ExecutorController {
    final WorkerService workerService;

    public ExecutorController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping("/dtss/work/api/v1/executeSubTask")
    public String executeSubTask(String txId, String taskPid, String subTaskName) {
        String name = "事务《"
                + txId.replaceFirst("^(...).*(...)$", "$1...$2")
                + "-" + taskPid.replaceFirst("^(...).*(...)$", "$1...$2")
                + "-" + subTaskName + "》";
        log.info(name + "即将执行");

        workerService.newTask(txId, taskPid, subTaskName);
        return "{\"status\":1}";
    }

}
