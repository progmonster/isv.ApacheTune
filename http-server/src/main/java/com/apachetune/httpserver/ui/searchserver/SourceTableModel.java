package com.apachetune.httpserver.ui.searchserver;

import com.apachetune.core.ResourceManager;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.apache.commons.lang.Validate.notNull;

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

    private final ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(SourceTableModel.class);

    private final String[] columnNames = {
            resourceBundle.getString("sourceTableModel.searchSourceTable.isIncludedColumnName"),
            resourceBundle.getString("sourceTableModel.searchSourceTable.searchLocationColumnName")
    };
    
    @SuppressWarnings({"serial"})
    private final List<Object[]> data = new ArrayList<Object[]>() {{
        add(new Object[] {true, resourceBundle.getString("sourceTableModel.searchSource.defaultInstallPath")});
        add(new Object[] {true, resourceBundle.getString("sourceTableModel.searchSource.insidePathSystemVariable")});
    }};

    public SourceTableModel(List<File> drivesAvailableToSearch) {
        notNull(drivesAvailableToSearch, "Argument drivesAvailableToSearch cannot be a null"); //NON-NLS

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
