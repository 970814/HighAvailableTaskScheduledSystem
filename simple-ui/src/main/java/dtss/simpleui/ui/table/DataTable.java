package dtss.simpleui.ui.table;

import dtss.simpleui.ui.windows.TableWindows;

import javax.swing.*;

public abstract class DataTable extends JTable {


    public abstract void close();

    public abstract String getTileString();

    protected TableWindows tableWindows;
    public void setContext(TableWindows tableWindows) {
        this.tableWindows = tableWindows;
    }
}
