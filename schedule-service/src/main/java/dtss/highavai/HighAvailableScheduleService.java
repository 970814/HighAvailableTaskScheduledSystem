package dtss.highavai;

import dtss.election.zkutil.ScheduleServiceMonitor;
import dtss.election.zkutil.ServiceRegistrationClient;
import dtss.scheduled.bean.ScheduleTask;
import dtss.scheduled.db.TaskDbUtil;
import dtss.scheduled.demo.DemoTaskToDb;
import dtss.scheduled.schedule.ScheduleService;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HighAvailableScheduleService {
    ScheduleService scheduleService;
    ServiceRegistrationClient serRegCli;
    ScheduleServiceMonitor monitor;
    final ScheduledExecutorService scheduledExecutorService;
    String scheduledNodeId;

    public HighAvailableScheduleService(int scheduledNodeId) {
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        monitor = new ScheduleServiceMonitor();
        monitor.connectZk();

        serRegCli = new ServiceRegistrationClient(this.scheduledNodeId = String.valueOf(scheduledNodeId));
        serRegCli.connectZk();
        serRegCli.saveServerInfo();
        serRegCli.registerAsLeader(this::monitorScheduleServiceStatus);

        scheduleService = new ScheduleService(scheduledNodeId);


    }

    private void monitorScheduleServiceStatus() {
        scheduledExecutorService
                .scheduleAtFixedRate(this::run,
                        1, 10, TimeUnit.SECONDS);
    }

    public void run() {
        try {
            run0();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    @SneakyThrows
    private void run0() {
        Set<Integer> ids = new HashSet<>();
        for (String id : monitor.getServerInfos()) ids.add(Integer.valueOf(id));
        System.out.println("当前可用调度节点列表 -> " + ids);
        List<ScheduleTask> scheduleTasks = TaskDbUtil.selectEnabledScheduleTask();
        for (ScheduleTask st : scheduleTasks) {
            if (st.getScheduledNodeId() == null || !ids.contains(st.getScheduledNodeId())) {
                Integer id = monitor.selectRandomScheduledNodeId();
                if (id != null) {
                    TaskDbUtil.executeTransaction(conn -> DemoTaskToDb.enabledScheduleTaskDemo(conn, st.getTaskId(), id));
                    System.out.println("由于" + st.getScheduledNodeId() + "号节点不可用，将任务《" + st.getName() + "》转移到" + id + "号节点继续执行");
                }
            }
        }
    }
}

