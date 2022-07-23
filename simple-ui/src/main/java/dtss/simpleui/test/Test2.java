package dtss.simpleui.test;

import dtss.simpleui.zkutil.ScheduleServiceMonitor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test2 {
    public static void main(String[] args) {
        ScheduleServiceMonitor monitor = new ScheduleServiceMonitor();
        monitor.connectZk();
        new JFrame(){
            {
                setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                setSize(500, 200);

                setLocationRelativeTo(null);

                add(new MonitorPanel(monitor));

            }
        }.setVisible(true);
    }


}
