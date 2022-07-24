package dtss.simpleui.bean;

import com.sun.deploy.panel.JreTableModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.table.TableColumnModel;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class ExecutionRecord {
    String txId;
    String taskId;
    String subTaskId;
    String startDatetime;
    String endDatetime;
    Integer costTime; //执行花费时间(秒)
    String result;
    Integer retryCount;
    String log;

    public static List<String> getTaskColumnNames() {
        return Arrays.asList(
                "事务Id",
                "任务Id",
//                "重试次数",
                "开始时间",
                "结束时间",
                "执行消耗时间",
                "执行结果"
        );
    }

    public String[] toTaskRow() {
        return new String[]{
                txId == null ? "/" : txId.replaceFirst("^(...).*(...)$","$1...$2"),
                taskId == null ? "/" : taskId.replaceFirst("^(...).*(...)$","$1...$2"),
//                String.valueOf(retryCount),
                startDatetime == null ? "/" : startDatetime,
                endDatetime == null ? "/" : endDatetime,
                costTime == null ? "/" : String.valueOf(costTime),
                result == null ? "/" : result,
        };
    }

    public static List<String> getColumnNames() {
        return Arrays.asList(
                "事务Id",
                "任务Id",
                "子任务Id",
                "重试次数",
                "开始时间",
                "结束时间",
                "耗时",
                "执行结果",
                "日志"
        );
    }

//    public static void adjustColumnWidth(TableColumnModel model) {
//        model.getColumn(0).setPreferredWidth(10);
//        model.getColumn(1).setPreferredWidth(10);
//        model.getColumn(2).setPreferredWidth(3);
//
//        model.getColumn(3).setPreferredWidth(20);
//        model.getColumn(4).setPreferredWidth(20);
//        model.getColumn(5).setPreferredWidth(10);
//        model.getColumn(6).setPreferredWidth(10);
//
//    }

    public String[] toRow() {
        return new String[]{
                txId == null ? "/" : txId.replaceFirst("^(...).*(...)$","$1...$2"),
                taskId == null ? "/" : taskId.replaceFirst("^(...).*(...)$","$1...$2"),
                subTaskId == null ? "/" : subTaskId,
                String.valueOf(retryCount),
                startDatetime == null ? "/" : startDatetime,
                endDatetime == null ? "/" : endDatetime,
                costTime == null ? "/" : String.valueOf(costTime),
                result == null ? "/" : result,
                log == null ? "/" : log,
        };
    }

}
