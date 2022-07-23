package dtss.simpleui.ui.windows;

import dtss.simpleui.test.MonitorPanel;
import dtss.simpleui.ui.table.DataTable;
import dtss.simpleui.ui.table.impl.TaskTable;
import dtss.simpleui.zkutil.ScheduleServiceMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TableWindows extends JFrame {
    ScheduleServiceMonitor monitor;

    public ScheduleServiceMonitor getMonitor() {
        return monitor;
    }

//    public void setMonitor(ScheduleServiceMonitor monitor) {
//        this.monitor = monitor;
//    }

    public TableWindows(ScheduleServiceMonitor monitor, DataTable dataTable, int width, int height) {
        this(dataTable, width, height);
        add(new MonitorPanel(this.monitor = monitor), BorderLayout.SOUTH);
    }

    public TableWindows(DataTable dataTable, int width, int height) {
        dataTable.setContext(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setSize(width, height);


        add(dataTable,BorderLayout.CENTER);
//        add(new MonitorPanel(monitor), BorderLayout.SOUTH);


        setTitle(dataTable.getTileString());


        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("close");
                dataTable.close();
            }
        });
        setVisible(true);


    }

    public static void main(String[] args) {
        ScheduleServiceMonitor monitor = new ScheduleServiceMonitor();
        monitor.connectZk();

//        String leaderId = monitor.getLeaderId();
//        List<String> serverInfos = monitor.getServerInfos();

//        System.out.println(leaderId);
//        System.out.println(serverInfos);





        TableWindows tableWindows = new TableWindows(monitor,new TaskTable(), 700, 200);

    }
}
