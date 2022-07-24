package dtss.election.zkutil;
import dtss.tp.ThreadPool;
import org.I0Itec.zkclient.ZkClient;

//将一个服务地址注册到zookeeper上
public class ServiceRegistrationClient {
    String scheduledNodeId;
    public ServiceRegistrationClient(String scheduledNodeId) {
        this.scheduledNodeId = scheduledNodeId;
        rootPath = "/schedule-service";
    }


    String rootPath;
    ZkClient zkClient;
    public void connectZk() {
        zkClient = new ZkClient("localhost:2181");

        if (!zkClient.exists(rootPath)) zkClient.createPersistent(rootPath);
    }


    public void saveServerInfo() {
        String path = rootPath + "/node" + scheduledNodeId;
        zkClient.createEphemeral(path, scheduledNodeId);
//        System.out.println("向zk注册成功, 调度节点`" + path + "`开始调度任务");
        System.out.println("向zk注册成功, 调度节点`" + scheduledNodeId + "`开始调度任务");
    }


    boolean first = true;
    public void registerAsLeader(CallBack callBack) {
        ThreadPool.getThreadPool().execute(() -> {
            String path = rootPath + "/leaderNodeId";
            while (true) {
                try {
                    if (first) System.out.println("调度节点" + scheduledNodeId + "尝试成为leader");
                    if (zkClient.exists(path)) {
                        if (first) System.out.println("leader节点已经存在");
                    } else {
                        try {
                            zkClient.createEphemeral(path, scheduledNodeId);
                            System.out.println("调度节点" + scheduledNodeId + "成功成为leader");
                            callBack.callback();
                            break;
                        } catch (Throwable e) {
                            System.out.println("Exception -> " + e);
                            System.out.println("leader节点已经存在");
                        }

                    }
                    first = false;
                    //noinspection BusyWait
                    Thread.sleep(3 * 1000);
                } catch (Throwable e) {
                    System.out.println("Exception -> " + e);
                    System.exit(1);
                }

            }
        });
    }


}
