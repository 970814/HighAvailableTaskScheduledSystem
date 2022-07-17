package dtss.simpleui.ui.table.impl;

import dtss.simpleui.background.DBUtils;
import dtss.simpleui.background.JTableRealTimeUpdateUtils;
import dtss.simpleui.bean.ExecutionRecord;
import dtss.simpleui.test.RowHeaderTable;
import dtss.simpleui.ui.table.DataTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.Timer;
import java.util.*;

public class SubTaskHistoryTable extends DataTable {
    Timer timer = new Timer();
    String taskId;
    String name;
    String txId;
    public SubTaskHistoryTable(String txId,String taskId, String name) {
        this.taskId = taskId;
        this.name = name;
        this.txId = txId;
        GroupLayout contentPaneLayout = new GroupLayout(this);
        setLayout(contentPaneLayout);
        JScrollPane scrollPane = new JScrollPane();

        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addContainerGap())
        );
        DefaultTableModel model = new DefaultTableModel(null, new Vector<>(ExecutionRecord.getColumnNames()));
        JTable jTable = new JTable(model);

        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(JLabel.CENTER);
        jTable.setDefaultRenderer(Object.class, tcr);
        jTable.getTableHeader().setDefaultRenderer(tcr);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<String[]> data = new ArrayList<>();
                for (ExecutionRecord r : DBUtils.showSubTaskExecuteHistory(txId, taskId)) data.add(r.toRow());
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
        return "事务" + txId.replaceFirst("^(...).*(...)$", "($1...$2)") + " 执行明细";
    }

}
