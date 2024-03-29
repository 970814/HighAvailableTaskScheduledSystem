package dtss.election.zkutil;


import lombok.SneakyThrows;
import org.I0Itec.zkclient.ZkClient;

import java.security.SecureRandom;
import java.util.*;

//用于访问zookeeper 获取当前可用的计算节点列表
public class ScheduleServiceMonitor {
    ZkClient zkClient;
    List<String> serverInfos;
    String rootPath;
    String leaderId;

    public String getLeaderId() {
        return leaderId;
    }

    public ScheduleServiceMonitor() {
        rootPath = "/schedule-service";
    }

    public void connectZk() {
        zkClient = new ZkClient("localhost:2181");
        List<String> children = zkClient.getChildren(rootPath);
        parseServerInfo(children);
        zkClient.subscribeChildChanges(rootPath, (path, list) -> {
            parseServerInfo(list);
            System.out.println("调度节点变化 " + path + " 发生改变 -> " + serverInfos);
        });

    }

    //    同步锁
    private synchronized void parseServerInfo(List<String> children) {

        List<String> infos = new ArrayList<String>();
        for (String child : children) {
            String nodeId = zkClient.readData(rootPath + "/" + child).toString();
            if (child.startsWith("node")) {
                infos.add(nodeId);
            } else {
                leaderId = nodeId;
            }
        }
        serverInfos = infos;
    }

    public synchronized List<String> getServerInfos() {
        return new ArrayList<>(serverInfos);
    }

    @SneakyThrows
    public static void main(String[] args) {
        ScheduleServiceMonitor scheduleServiceMonitor = new ScheduleServiceMonitor();
        scheduleServiceMonitor.connectZk();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Leader节点 —> " + scheduleServiceMonitor.getLeaderId() + ", 调度节点列表 —> " + scheduleServiceMonitor.getServerInfos());
            }
        }, 0, 5000);
    }

    //    随机挑选一个节点id
    public  Integer selectRandomScheduledNodeId() {
        SecureRandom secRnd = new SecureRandom();
        List<String> ids = getServerInfos();

        return ids.size() == 0 ? null : Integer.valueOf(ids.get(secRnd.nextInt(ids.size())));
    }

}
