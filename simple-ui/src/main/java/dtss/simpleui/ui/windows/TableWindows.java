package dtss.simpleui.ui.windows;

import dtss.simpleui.test.Test;
import dtss.simpleui.ui.table.DataTable;
import dtss.simpleui.ui.table.impl.TaskTable;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TableWindows extends JFrame {

    public TableWindows(DataTable dataTable, int width, int height) {

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setSize(width, height);


        Container contentPane = getContentPane();
        contentPane.add(dataTable);


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
//        new TableWindows(new HistoryTable());
        new TableWindows(new TaskTable(), 700, 200);
    }
}
