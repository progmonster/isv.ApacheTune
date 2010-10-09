package com.apachetune.httpserver.ui.searchserver;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
class SourceTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 3788873223453720686L;

    public static final int FIRST_FIXED_ROWS_COUNT = 2;

    public static final int LOCATION_NAME_COLUMN_IDX = 1;

    public static final int SELECT_LOCATION_COLUMN_IDX = 0;

    private final String[] columnNames = {"Is included", "Search location"}; // TODO Localize.
    
    @SuppressWarnings({"serial"})
    private final List<Object[]> data = new ArrayList<Object[]>() {{
        add(new Object[] {true, "Default install path"}); // TODO Localize.
        add(new Object[] {true, "Inside PATH system variable"}); // TODO Localize.
    }};

    public SourceTableModel(List<File> drivesAvailableToSearch) {
        if (drivesAvailableToSearch == null) {
            throw new NullPointerException("Argument drivesAvailableToSearch cannot be a null [this = " + this + "]");
        }

        for (File file : drivesAvailableToSearch) {
            data.add(new Object[] {true, file});
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(SELECT_LOCATION_COLUMN_IDX, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return (row >= FIRST_FIXED_ROWS_COUNT) && (col == SELECT_LOCATION_COLUMN_IDX);
    }

    public void setValueAt(Object value, int row, int col) {
        data.get(row)[col] = value;
        fireTableCellUpdated(row, col);
    }
}
