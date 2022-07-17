package dtss.election.demo;

import dtss.election.zkutil.ServiceRegistrationClient;
import lombok.SneakyThrows;

public class ServerDemo2 {
    @SneakyThrows
    public static void main(String[] args) {
        ServiceRegistrationClient serRegCli = new ServiceRegistrationClient("2");
        serRegCli.connectZk();
        serRegCli.saveServerInfo();
        serRegCli.registerAsLeader();
        Thread.sleep(10000000);
    }
}
