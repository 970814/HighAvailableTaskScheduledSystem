package dtss.simpleui.ui.table.impl;

import dtss.simpleui.background.DBUtils;
import dtss.simpleui.background.JTableRealTimeUpdateUtils;
import dtss.simpleui.bean.ScheduleTask;
import dtss.simpleui.test.RowHeaderTable;
import dtss.simpleui.ui.table.DataTable;
import dtss.simpleui.ui.windows.TableWindows;
import dtss.simpleui.zkutil.ScheduleServiceMonitor;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.*;

public class TaskTable extends DataTable {

    Timer timer = new Timer();
    final JTable jTable;

    String taskId;
    String name;
    int selectedRow;
    ScheduleServiceMonitor monitor = new ScheduleServiceMonitor();
    public TaskTable() {
        monitor.connectZk();
        GroupLayout contentPaneLayout = new GroupLayout(this);
        setLayout(contentPaneLayout);
        JScrollPane scrollPane = new JScrollPane();

        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addContainerGap())
        );
        DefaultTableModel model = new DefaultTableModel(null, new Vector<>(ScheduleTask.getColumnNames()));
        jTable = new JTable(model);
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(JLabel.CENTER);
        jTable.setDefaultRenderer(Object.class, tcr);
        jTable.getTableHeader().setDefaultRenderer(tcr);

        JPopupMenu menu = new JPopupMenu("menu");
        menu.add("1.启用").addActionListener(e-> DBUtils.enabledScheduleTaskDemo(taskId,monitor.selectRandomScheduledNodeId()));
        menu.add("2.关闭").addActionListener(e -> DBUtils.disabledScheduleTaskDemo(taskId, null));
        menu.add("3.子任务列表").addActionListener(e -> {
            if (taskId != null && name != null) {

                new TableWindows(new SubTaskTable(taskId, name), 500, 200);
            }
        });
        menu.add("4.运行历史")
                .addActionListener(e -> new TableWindows(
                                new HistoryTable(taskId, name),
                                1200, 205));
        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("按键值：" + e.getButton());
                if (e.getButton() == MouseEvent.BUTTON3) {
                    selectedRow = jTable.getSelectedRow();
                    if (selectedRow >= 0) menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        timer.schedule(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                List<String[]> data = new ArrayList<>();
                List<ScheduleTask> scheduleTasks = DBUtils.selectTaskList();
                for (ScheduleTask r : scheduleTasks) data.add(r.toRow());
                int i = selectedRow;
                if (i >= 0 && i < data.size()) {
                    name = data.get(i)[0];
                    taskId = scheduleTasks.get(i).getTaskId();
                }
                JTableRealTimeUpdateUtils.update(data, jTable, model);
                scrollPane.setRowHeaderView(new RowHeaderTable(jTable, 40));
            }
        }, 0, 1000);
        scrollPane.setViewportView(jTable);
    }

    public void close() {
        timer.cancel();
    }

    @Override
    public String getTileString() {
        return "定时任务列表";
    }



}
