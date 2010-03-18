package com.apachetune.httpserver.ui.smartparts.searchserver;

import com.apachetune.core.WorkItem;
import com.apachetune.core.impl.RootWorkItemImpl;
import com.apachetune.httpserver.impl.HttpServerManagerImpl;
import com.google.inject.Inject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.apachetune.core.utils.Utils.abbreviateFilePath;
import static com.apachetune.httpserver.ui.smartparts.searchserver.SourceTableModel.LOCATION_NAME_COLUMN_IDX;
import static com.apachetune.httpserver.ui.smartparts.searchserver.SourceTableModel.SELECT_LOCATION_COLUMN_IDX;

public class SearchServerSmartPart extends JDialog implements SearchServerDialog {
    private static final int MAX_LOCATION_TEXT_SIZE = 60;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton searchButton;
    private JTable sourceTable;
    private JScrollPane sourceTableScrollPane;
    private JButton stopSearchButton;
    private JProgressBar searchProgressBar;
    private JList resultList;

    private JLabel currentSearchLabel;

    private final JFrame mainFrame;

    private final SearchServerPresenter presenter;

    private final List<File> drivesAvailableToSearch = new ArrayList<File>();

    @Inject
    public SearchServerSmartPart(final SearchServerPresenter presenter, JFrame mainFrame) {
        super(mainFrame);

        if (presenter == null) {
            throw new NullPointerException("Argument presenter cannot be a null [this = " + this + "]");
        }

        this.mainFrame = mainFrame;
        this.presenter = presenter;

        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        setTitle("Search for HTTP-server"); // TODO Localize.
        getRootPane().setDefaultButton(buttonOK);

        sourceTable.setVisible(true);
        sourceTable.setFillsViewportHeight(true);
        sourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourceTable.getTableHeader().setReorderingAllowed(false);

        searchProgressBar.setString("");

        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setVisibleRowCount(6);

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                presenter.onCancel();
            }
        }
        );

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.onStartSearch();
            }
        }
        );
        stopSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.onStopSearch();
            }
        }
        );
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.onSelectServer();
            }
        }
        );
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.onCancel();
            }
        }
        );
        resultList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    presenter.onResultSelected(resultList.getLeadSelectionIndex());
                }
            }
        }
        );
    }

    public void setDrivesAvailableToSearch(Collection<File> drives) {
        if (drives == null) {
            throw new NullPointerException("Argument drives cannot be a null [this = " + this + "]");
        }

        drivesAvailableToSearch.clear();

        drivesAvailableToSearch.addAll(drives);
    }

    public List<File> getSelectedDrivesToSearch() {
        SourceTableModel sourceTableModel = (SourceTableModel) sourceTable.getModel();

        List<File> selectedDrives = new ArrayList<File>(drivesAvailableToSearch.size());

        for (int srcTableRowIdx = SourceTableModel.FIRST_FIXED_ROWS_COUNT; srcTableRowIdx < sourceTableModel
                .getRowCount(); srcTableRowIdx++) {
            Boolean isDriveSelected = (Boolean) sourceTableModel.getValueAt(srcTableRowIdx, SELECT_LOCATION_COLUMN_IDX);

            if (isDriveSelected) {
                File selectedDrive = (File) sourceTableModel.getValueAt(srcTableRowIdx, LOCATION_NAME_COLUMN_IDX);

                selectedDrives.add(selectedDrive);
            }
        }

        return selectedDrives;
    }

    public void setSourceTableEnabled(boolean isEnabled) {
        sourceTable.setEnabled(isEnabled);
    }

    public void setStartSearchButtonEnabled(boolean isEnabled) {
        searchButton.setEnabled(isEnabled);
    }

    public void setStopSearchButtonEnabled(boolean isEnabled) {
        stopSearchButton.setEnabled(isEnabled);
    }

    public void setSelectServerButtonEnabled(boolean isEnabled) {
        buttonOK.setEnabled(isEnabled);
    }

    public void setResultListModel(ResultListModel model) {
        if (model == null) {
            throw new NullPointerException("Argument model cannot be a null [this = " + this + "]");
        }

        resultList.setModel(model);
    }

    public void clearSearchResults() {
        ResultListModel resultListModel = (ResultListModel) resultList.getModel();

        resultListModel.clear();
    }

    public void setSearchProgressBarRun(boolean isRun) {
        searchProgressBar.setIndeterminate(isRun);
    }

    public void setCurrentSearchLocationText(String location) {
        if (location == null) {
            throw new NullPointerException("Argument location cannot be a null [this = " + this + "]");
        }

        String abbreviatedLocation = abbreviateFilePath(location, MAX_LOCATION_TEXT_SIZE);

        currentSearchLabel.setText(abbreviatedLocation);
    }

    public void selectFirstResult() {
        if (resultList.getModel().getSize() > 0) {
            resultList.setSelectedIndex(0);
        }
    }

    public void initialize(WorkItem workItem) {
        if (workItem == null) {
            throw new NullPointerException("Argument workItem cannot be a null [this = " + this + "]");
        }

        presenter.initialize(workItem, this);

        sourceTable.setModel(new SourceTableModel(drivesAvailableToSearch));

        Dimension sourceTableScrollPaneSize = sourceTableScrollPane.getPreferredSize();

        sourceTableScrollPaneSize.setSize(sourceTableScrollPaneSize.getWidth(), sourceTable.getRowHeight() *
                (sourceTable.getRowCount() + 1 + 1)
        ); // getRowCount() + header row and one blank row.

        sourceTableScrollPane.setPreferredSize(sourceTableScrollPaneSize);

        pack();

        setSourceTableColumnSize();

        setLocationRelativeTo(mainFrame);
    }

    public void run() {
        setVisible(true);
    }

    private void setSourceTableColumnSize() {
        sourceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn selectLocationColumn = sourceTable.getColumnModel().getColumn(LOCATION_NAME_COLUMN_IDX);

        selectLocationColumn.setPreferredWidth(100);

        TableColumn locationNameColumn = sourceTable.getColumnModel().getColumn(LOCATION_NAME_COLUMN_IDX);

        locationNameColumn.setPreferredWidth(300);

        sourceTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    public static void main(String[] args) {
        WorkItem workItem = new RootWorkItemImpl();

        SearchServerPresenter presenter = new SearchServerPresenter(null, new HttpServerManagerImpl());

        SearchServerSmartPart view = new SearchServerSmartPart(presenter, null);

        view.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        presenter.initialize(workItem, view);
        view.initialize(workItem);

        view.run();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1,
                        new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                            1, null, null, null, 0, false
                        )
        );
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1,
                   new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                       GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false
                   )
        );
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                               GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                               GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                               null, null, null, 0, false
        )
        );
        buttonOK = new JButton();
        buttonOK.setText("Select");
        buttonOK.setMnemonic('S');
        buttonOK.setDisplayedMnemonicIndex(0);
        panel2.add(buttonOK,
                   new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                       GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                       GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false
                   )
        );
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        buttonCancel.setMnemonic('C');
        buttonCancel.setDisplayedMnemonicIndex(0);
        panel2.add(buttonCancel,
                   new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                       GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                       GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false
                   )
        );
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3,
                        new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                            null, null, null, 0, false
                        )
        );
        final JLabel label1 = new JLabel();
        label1.setText("Search results:");
        panel3.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                               GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                                               null, null, 0, false
        )
        );
        stopSearchButton = new JButton();
        stopSearchButton.setText("Stop");
        stopSearchButton.setMnemonic('T');
        stopSearchButton.setDisplayedMnemonicIndex(1);
        panel3.add(stopSearchButton,
                   new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                       GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                       GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false
                   )
        );
        searchProgressBar = new JProgressBar();
        searchProgressBar.setIndeterminate(false);
        searchProgressBar.setOpaque(true);
        searchProgressBar.setString("");
        searchProgressBar.setStringPainted(true);
        panel3.add(searchProgressBar,
                   new GridConstraints(0, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                       GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null,
                                       null, null, 0, false
                   )
        );
        searchButton = new JButton();
        searchButton.setText("Search");
        searchButton.setMnemonic('E');
        searchButton.setDisplayedMnemonicIndex(1);
        panel3.add(searchButton,
                   new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                       GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                       GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false
                   )
        );
        currentSearchLabel = new JLabel();
        currentSearchLabel.setFont(new Font(currentSearchLabel.getFont().getName(), Font.PLAIN, 12));
        currentSearchLabel.setText("<currentSearchLabel>");
        panel3.add(currentSearchLabel,
                   new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                       GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                       new Dimension(-1, 20), null, null, 0, false
                   )
        );
        final JLabel label2 = new JLabel();
        label2.setText("Where to search:");
        contentPane.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                                    GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                                                    null, null, null, 0, false
        )
        );
        sourceTableScrollPane = new JScrollPane();
        contentPane.add(sourceTableScrollPane,
                        new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                            null, null, null, 0, false
                        )
        );
        sourceTable = new SourceTable();
        sourceTable.setVisible(false);
        sourceTableScrollPane.setViewportView(sourceTable);
        final JScrollPane scrollPane1 = new JScrollPane();
        contentPane.add(scrollPane1,
                        new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                            null, null, null, 0, false
                        )
        );
        resultList = new JList();
        scrollPane1.setViewportView(resultList);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
