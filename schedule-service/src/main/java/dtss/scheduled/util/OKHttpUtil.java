package dtss.scheduled.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import dtss.scheduled.bean.Req;
import lombok.SneakyThrows;
import okhttp3.*;

import java.util.Objects;
import java.util.Random;

public class OKHttpUtil {
    static OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {
        Req req = new Req("",
                "DAF807637D2BC0318C474F66C8DE119BB072ACAEBACD9AB23825CB199E8D9DB6",
                "main", 0, "sh execJar.sh");
        System.out.println(submitTaskToWorkerService(req));
    }

    public static String submitTaskToWorkerService(Req req) {

        return submitTaskToWorkerService(client, req);

    }

    @SneakyThrows
    public static String submitTaskToWorkerService(OkHttpClient okHttpClient, Req req) {
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json;charset=UTF-8")
                .url("http://localhost:8080/dtss/work/api/v1/executeSubTask")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                        new ObjectMapper().writeValueAsString(req)))
                .build();

        return get(okHttpClient, request);
    }
    public static String get(OkHttpClient okHttpClient, Request request) {
        final int maxRetryCount = 100;
        Random rnd = new Random();
        for (int i = 0; i < maxRetryCount; i++) {
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return Objects.requireNonNull(response.body()).string();
                } else {
                    final String string = Objects.requireNonNull(response.body()).string();
                    System.err.println("failed retry " + (i + 1) + " case `" + string + "`");
                }
            } catch (Exception e) {
                System.err.println("failed retry2 " + (i + 1) + e);
            }
            if (i < maxRetryCount - 1)
                try {
                    final long delayMills = (5) * 1000 + ((long) i * i ) * 500L + rnd.nextInt(1000);
                    System.out.println("将睡眠({})ms继续下载" + delayMills);
                    Thread.sleep(delayMills);
                } catch (InterruptedException e) {
                    System.err.println("InterruptedException: "+e);
                    System.exit(1);
                }
        }
        throw new RuntimeException("Exceeded max retry count limit(" + maxRetryCount + ")");
    }


}