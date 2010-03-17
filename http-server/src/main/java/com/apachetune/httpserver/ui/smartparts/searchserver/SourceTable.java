package com.apachetune.httpserver.ui.smartparts.searchserver;

// TODO remove sun.swing.* dependence.

import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class SourceTable extends JTable {
    private final TextCellRenderer textCellRenderer = new TextCellRenderer();

    private final DisabledBooleanCellRenderer disabledBooleanCellRenderer = new DisabledBooleanCellRenderer();

    public TableCellRenderer getCellRenderer(int row, int column) {
        if (row < SourceTableModel.FIRST_FIXED_ROWS_COUNT) {
            if (column == SourceTableModel.LOCATION_NAME_COLUMN_IDX) {
                return textCellRenderer;
            } else if (column == SourceTableModel.SELECT_LOCATION_COLUMN_IDX){
                return disabledBooleanCellRenderer;
            } else {
                return super.getCellRenderer(row, column);
            }
        } else {
            return super.getCellRenderer(row, column);
        }
    }
}

class TextCellRenderer extends JLabel implements TableCellRenderer, UIResource {
     private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
     private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
     protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;

     private Color unselectedForeground;
     private Color unselectedBackground;

     public TextCellRenderer() {
     super();
     setOpaque(true);
         setBorder(getNoFocusBorder());
         setName("Table.cellRenderer");
     }

    private Border getNoFocusBorder() {
         Border border = DefaultLookup.getBorder(this, ui, "Table.cellNoFocusBorder");
         if (System.getSecurityManager() != null) {
             if (border != null) return border;
             return SAFE_NO_FOCUS_BORDER;
         } else if (border != null) {
             if (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER) {
                 return border;
             }
         }
         return noFocusBorder;
     }

     public void setForeground(Color c) {
         super.setForeground(c);
         unselectedForeground = c;
     }

     public void setBackground(Color c) {
         super.setBackground(c);
         unselectedBackground = c;
     }

     public void updateUI() {
         super.updateUI();
     setForeground(null);
     setBackground(null);
     }

     public Component getTableCellRendererComponent(JTable table, Object value,
                           boolean isSelected, boolean hasFocus, int row, int column) {

         Color fg = null;
         Color bg = null;

         JTable.DropLocation dropLocation = table.getDropLocation();
         if (dropLocation != null
                 && !dropLocation.isInsertRow()
                 && !dropLocation.isInsertColumn()
                 && dropLocation.getRow() == row
                 && dropLocation.getColumn() == column) {

             fg = DefaultLookup.getColor(this, ui, "Table.dropCellForeground");
             bg = DefaultLookup.getColor(this, ui, "Table.dropCellBackground");

             isSelected = true;
         }

         if (isSelected) {
             super.setForeground(fg == null ? table.getSelectionForeground()
                                            : fg);
             super.setBackground(bg == null ? table.getSelectionBackground()
                                            : bg);
     } else {
             Color background = unselectedBackground != null
                                     ? unselectedBackground
                                     : table.getBackground();
             if (background == null || background instanceof javax.swing.plaf.UIResource) {
                 Color alternateColor = DefaultLookup.getColor(this, ui, "Table.alternateRowColor");
                 if (alternateColor != null && row % 2 == 0)
                     background = alternateColor;
             }
             super.setForeground(unselectedForeground != null
                                     ? unselectedForeground
                                     : table.getForeground());
             super.setBackground(background);
     }

     Font defaultFont = table.getFont();

     if ((row < SourceTableModel.FIRST_FIXED_ROWS_COUNT) && (column == SourceTableModel.LOCATION_NAME_COLUMN_IDX)) {
        setFont(new Font(defaultFont.getName(), Font.ITALIC, defaultFont.getSize()));
     } else {
         setFont(defaultFont);
     }

     if (hasFocus) {
             Border border = null;
             if (isSelected) {
                 border = DefaultLookup.getBorder(this, ui, "Table.focusSelectedCellHighlightBorder");
             }
             if (border == null) {
                 border = DefaultLookup.getBorder(this, ui, "Table.focusCellHighlightBorder");
             }
             setBorder(border);

         if (!isSelected && table.isCellEditable(row, column)) {
                 Color col;
                 col = DefaultLookup.getColor(this, ui, "Table.focusCellForeground");
                 if (col != null) {
                     super.setForeground(col);
                 }
                 col = DefaultLookup.getColor(this, ui, "Table.focusCellBackground");
                 if (col != null) {
                     super.setBackground(col);
                 }
         }
     } else {
             setBorder(getNoFocusBorder());
     }

         setValue(value);

     return this;
     }

     public boolean isOpaque() {
     Color back = getBackground();
     Component p = getParent();
     if (p != null) {
         p = p.getParent();
     }

     // p should now be the JTable.
     boolean colorMatch = (back != null) && (p != null) &&
         back.equals(p.getBackground()) &&
             p.isOpaque();
     return !colorMatch && super.isOpaque();
     }

     public void invalidate() {}

     public void validate() {}

     public void revalidate() {}

     public void repaint(long tm, int x, int y, int width, int height) {}

     public void repaint(Rectangle r) { }

     public void repaint() {
     }

     protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
     // Strings get interned...
     if (propertyName.equals("text")
                 || propertyName.equals("labelFor")
                 || propertyName.equals("displayedMnemonic")
                 || ((propertyName.equals("font") || propertyName.equals("foreground"))
                     && oldValue != newValue
                     && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {

             super.firePropertyChange(propertyName, oldValue, newValue);
         }
     }

     public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) { }


     protected void setValue(Object value) {
         if (value == null) {
             setText("");
         } else if (value instanceof String) {
             setText((String) value);
         } else if (value instanceof File) {
             setText(((File) value).getAbsolutePath());
         }

     setText((value == null) ? "" : value.toString());
     }


     public static class UIResource extends DefaultTableCellRenderer
         implements javax.swing.plaf.UIResource
     {
     }
}

class DisabledBooleanCellRenderer extends JCheckBox implements TableCellRenderer, UIResource
{
    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);


    public DisabledBooleanCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
        setBorderPainted(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        setEnabled(false);

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        }
        else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        setSelected((value != null) && (Boolean) value);

        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(noFocusBorder);
        }

        return this;
    }
}