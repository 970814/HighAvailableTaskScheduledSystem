package dtss.worker.workerservice.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {


    public static String currentCSTDateTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));//转换为北京时间
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }




}
