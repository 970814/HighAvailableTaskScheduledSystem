package dtss.simpleui.test;

import javax.swing.*;
import java.awt.*;

public class RowHeaderTable extends JTable
{   
    private JTable refTable;//需要添加rowHeader的JTable   
    /**  
     * 为JTable添加RowHeader，  
     * @param refTable 需要添加rowHeader的JTable    
     */
    public RowHeaderTable(JTable refTable,int columnWidth){   
        super(new RowHeaderTableModel(refTable.getRowCount()));   
        this.refTable=refTable;   
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);//不可以调整列宽   
        this.getColumnModel().getColumn(0).setPreferredWidth(columnWidth);   
        this.setDefaultRenderer(Object.class,new RowHeaderRenderer(refTable,this));//设置渲染器   
        this.setPreferredScrollableViewportSize (new Dimension(columnWidth,0));
    }   
}   