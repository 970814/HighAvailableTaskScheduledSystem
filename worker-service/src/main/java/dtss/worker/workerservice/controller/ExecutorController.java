package dtss.worker.workerservice.controller;

import com.oracle.tools.packager.Log;
import dtss.worker.workerservice.bean.Req;
import dtss.worker.workerservice.util.Utils;
import dtss.worker.workerservice.worker.WorkerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ExecutorController {
    final WorkerService workerService;

    public ExecutorController(WorkerService workerService) {
        this.workerService = workerService;
    }

    //    @PostMapping("/dtss/work/api/v1/executeSubTask")
    @PostMapping(value = "/dtss/work/api/v1/executeSubTask", produces = "application/json;charset=UTF-8")
    public String executeSubTask(@RequestBody Req req) {
        log.info(Utils.txName(req.getTxId(), req.getTaskPid(), req.getSubTaskName(), req.getRetryCount()) + "即将执行");
        workerService.newTask(req.getTxId(), req.getTaskPid(), req.getSubTaskName(), req.getRetryCount(), req.getCommand());
        return "{\"status\":1}";
    }

}
