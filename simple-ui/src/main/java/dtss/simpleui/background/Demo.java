package dtss.simpleui.background;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;


public class Demo extends JFrame
{
    public Demo()
    {
        initComponents();
    }

    private void initComponents()
    {
        scrollPane1 = new JScrollPane();
        table1 = new JTable()
        {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        Container contentPane = getContentPane();

        {
            scrollPane1.setViewportView(table1);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JScrollPane scrollPane1;
    private JTable table1;

    public static void main(String[] args)
    {
        Demo demo = new Demo();
        Random r = new Random();

        Vector head = new Vector();
        head.add("A");
        head.add("B");
        head.add("C");
        DefaultTableModel model = new DefaultTableModel(null, head);
        demo.table1.setModel(model);
        demo.setVisible(true);

        new Timer(1000, evt ->
        {
            List<String[]> list = new ArrayList<>();
            for (int i = 0; i < r.nextInt(10) + 1; i++)
            {
                list.add(new String[]{i + "", (i + 1) + "", (i + 2) + ""});
            }

            JTableRealTimeUpdateUtils.update(list, demo.table1, model);
        }).start();
    }
}


