package dtss.simpleui.ui.table.impl;

import dtss.simpleui.background.DBUtils;
import dtss.simpleui.background.JTableRealTimeUpdateUtils;
import dtss.simpleui.bean.ExecutionRecord;
import dtss.simpleui.bean.ScheduleTask;
import dtss.simpleui.test.RowHeaderTable;
import dtss.simpleui.ui.table.DataTable;
import dtss.simpleui.ui.windows.TableWindows;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.Timer;

public class HistoryTable extends DataTable {
    Timer timer = new Timer();
    String taskId;
    String txId;
    String name;
    int selectedRow;
    public HistoryTable(String taskId, String name) {
        this.taskId = taskId;
        this.name = name;
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
        DefaultTableModel model = new DefaultTableModel(null, new Vector<>(ExecutionRecord.getTaskColumnNames()));
        JTable jTable = new JTable(model);

        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(JLabel.CENTER);
        jTable.setDefaultRenderer(Object.class, tcr);
        jTable.getTableHeader().setDefaultRenderer(tcr);



        JPopupMenu menu = new JPopupMenu("menu");

        menu.add("5.详细运行历史")
                .addActionListener(e -> new TableWindows(
                        new SubTaskHistoryTable(txId,taskId, name),
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
            @Override
            public void run() {
                List<String[]> data = new ArrayList<>();
                List<ExecutionRecord> executionRecords = DBUtils.showTaskExecuteHistory(taskId);
                for (ExecutionRecord r : executionRecords) data.add(r.toTaskRow());
                int i = selectedRow;
                if (i >= 0 && i < data.size()) txId = executionRecords.get(i).getTxId();
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
        return name + taskId.replaceFirst("^(...).*(...)$", "($1...$2)") + " 的运行历史";
    }

}
