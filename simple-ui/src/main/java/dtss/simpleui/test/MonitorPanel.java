package dtss.simpleui.test;

import dtss.simpleui.zkutil.ScheduleServiceMonitor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MonitorPanel extends JPanel {
    public MonitorPanel(ScheduleServiceMonitor monitor) {

        List<JButton> buttonList = new ArrayList<>();


        setLayout(new GridLayout(1, 3, 8, 4));
        JButton b1 = getLabel();
        JButton b2 = getLabel();
        JButton b3 = getLabel();
        buttonList.add(b1);
        buttonList.add(b2);
        buttonList.add(b3);
        add(b1);
        add(b2);
        add(b3);

        monitor.setAction((ids, leaId) -> {
            System.out.println("callback");
            Set<String> idSet = new HashSet<>(ids);


            for (int i = 0; i < buttonList.size(); i++) {
                JButton jButton = buttonList.get(i);
                String id = String.valueOf(i + 1);
                jButton.setText(id + "-follower");
                if (idSet.contains(id)) {
                    if (leaId != null && id.equals(String.valueOf(leaId)))
                        jButton.setText(id + "-leader");
                    jButton.setBackground(Color.WHITE);
                } else {
                    jButton.setBackground(Color.GRAY);
                }


            }
        });
        monitor.updateServerList();

    }
    public static JButton getLabel() {
        return new JButton("66") {
            {
                setOpaque(true);
                setBorderPainted(false);
            }
        };
    }
}
