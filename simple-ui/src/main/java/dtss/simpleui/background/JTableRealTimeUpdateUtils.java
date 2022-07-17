package dtss.simpleui.background;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;


public class JTableRealTimeUpdateUtils
{

    public static void update(List<String[]> data, JTable table, DefaultTableModel tableModel)
    {
        update(data, tableModel);
        if (data.size() > tableModel.getRowCount())
        {
            int addSize = data.size() - tableModel.getRowCount();
            for (int i = 0; i < addSize; i++)
            {
                tableModel.addRow(data.get(tableModel.getRowCount()));
            }
        }
        else if(data.size() < tableModel.getRowCount())
        {
            int reduceSize = tableModel.getRowCount() - data.size();
            for (int i = 0; i < reduceSize; i++)
            {
                tableModel.removeRow(tableModel.getRowCount() - 1);
            }
        }
        table.validate();
    }

    private static void update(List<String[]> data, DefaultTableModel tableModel)
    {
        for (int i = 0; i < data.size(); i++)
        {
            String[] strArr = data.get(i);
            for (int j = 0; j < strArr.length; j++)
            {
                if (i > tableModel.getRowCount() - 1)
                {
                    break;
                }
                if (!strArr[j].equals(tableModel.getValueAt(i, j)))
                {
                    tableModel.setValueAt(strArr[j], i, j);
                }
            }
        }
    }
}


