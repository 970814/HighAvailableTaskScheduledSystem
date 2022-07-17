package dtss.simpleui.ui.table.impl;

import dtss.simpleui.background.DBUtils;
import dtss.simpleui.background.JTableRealTimeUpdateUtils;
import dtss.simpleui.bean.SubTask;
import dtss.simpleui.test.RowHeaderTable;
import dtss.simpleui.ui.table.DataTable;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.*;

public class SubTaskTable extends DataTable {
    Timer timer = new Timer();
    final JTable jTable;
    String taskPid;
    String name;

    public SubTaskTable(String taskPid, String name) {
        this.taskPid = taskPid;
        this.name = name;
        GroupLayout contentPaneLayout = new GroupLayout(this);
        setLayout(contentPaneLayout);
        JScrollPane scrollPane = new JScrollPane();

        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addContainerGap())
        );
        DefaultTableModel model = new DefaultTableModel(null, new Vector<>(SubTask.getColumnNames()));
        jTable = new JTable(model);

        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(JLabel.CENTER);
        jTable.setDefaultRenderer(Object.class, tcr);
        jTable.getTableHeader().setDefaultRenderer(tcr);

        timer.schedule(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                List<String[]> data = new ArrayList<>();
                for (SubTask r : DBUtils.selectSubTaskList(taskPid)) data.add(r.toRow());
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
        return name + taskPid.replaceFirst("^(...).*(...)$", "($1...$2)") + " 的子任务列表";
    }


}
