package util;

import java.util.HashSet;
import java.util.Set;

public class Utils {


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
}
