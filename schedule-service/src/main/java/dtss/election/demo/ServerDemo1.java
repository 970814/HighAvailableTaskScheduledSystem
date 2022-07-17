package dtss.election.demo;

import dtss.election.zkutil.ServiceRegistrationClient;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;


public class ServerDemo1 {
    @SneakyThrows
    public static void main(String[] args) {
        ServiceRegistrationClient serRegCli = new ServiceRegistrationClient("1");
        serRegCli.connectZk();
        serRegCli.saveServerInfo();
        serRegCli.registerAsLeader();
        Thread.sleep(10000000);

    }
}
