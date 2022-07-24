package dtss.worker.workerservice.util;


import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class Utils {


    public static String currentCSTDateTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));//转换为北京时间
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

//    public static void taskStartLog(String txId, String taskPid, String subTaskName, int retryCount) {
//        log.info(txName(txId, taskPid, subTaskName, retryCount) + "即将执行");
//    }
//
//    public static void taskEndLog(String txId, String taskPid, String subTaskName, int retryCount) {
//        log.info(txName( txId,  taskPid,  subTaskName, retryCount) + "执行完成");
//    }

    public static String txName(String txId, String taskPid, String subTaskName, int retryCount) {
        return "事务《"
                + txId.replaceFirst("^(...).*(...)$", "$1...$2")
                + "-" + taskPid.replaceFirst("^(...).*(...)$", "$1...$2")
                + "-" + subTaskName
                + "-" + retryCount
                + "》";
    }
}
