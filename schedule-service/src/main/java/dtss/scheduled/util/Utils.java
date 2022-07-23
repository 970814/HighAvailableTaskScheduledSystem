package dtss.scheduled.util;

import com.alibaba.druid.util.HexBin;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public class Utils {

    public static String generateRandomTransactionId() {
        return getRndHex(32);
    }
    public static String generateRandomTaskId() {
        return getRndHex(32);
    }

    //    生成随机任务Id
    private static String getRndHex(int n) {
        SecureRandom secRnd = new SecureRandom();
        byte[] sha256 = new byte[n];
        secRnd.nextBytes(sha256);
        return HexBin.encode(sha256);
    }

//    返回A和B的交集
    public static Set<String> intersection(Set<String> A, Set<String> B) {
        Set<String> res = new HashSet<>(A);
        res.retainAll(B);
        return res;
    }

//    返回A-B集合
    public static Set<String> difference(Set<String> A, Set<String> B) {
        Set<String> disabledTaskIds = new HashSet<>(A);
        disabledTaskIds.removeAll(B);
        return disabledTaskIds;
    }

    public static String currentCSTDateTimeStr(long tm) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));//转换为北京时间
        Date date = new Date(tm);
        return sdf.format(date);
    }




}
