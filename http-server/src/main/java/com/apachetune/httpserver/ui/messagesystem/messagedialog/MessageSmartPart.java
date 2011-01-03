package com.apachetune.httpserver.ui.messagesystem.messagedialog;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import com.apachetune.core.ResourceManager;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.ui.UIWorkItem;
import com.apachetune.httpserver.ui.messagesystem.MessageManager;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.google.inject.Inject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.*;
import java.util.List;

import static com.apachetune.core.ui.Constants.MESSAGE_SMART_PART_ID;
import static com.apachetune.core.ui.Utils.restoreDialogBounds;
import static com.apachetune.core.ui.Utils.storeDialogBounds;
import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;
import static org.apache.commons.lang.Validate.notNull;

public class MessageSmartPart extends JDialog implements MessageView, ListSelectionListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageSmartPart.class);

    private final MessagePresenter presenter;

    private final MessageManager messageManager;

    private final PreferencesManager preferencesManager;

    private final Set<NewsMessage> selectedMessages = new HashSet<NewsMessage>();

    private JPanel contentPane;

    private JButton buttonOK;

    private JWebBrowser webBrowser;

    private JButton selectButton;

    private JButton markAsUnreadButton;

    private JButton deleteButton;

    private JTable messageTable;

    private NewsMessage currentShowedMsg;

    private boolean isDisposed;

    private final ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(MessageSmartPart.class);

    @Inject
    public MessageSmartPart(final MessagePresenter presenter, MessageManager messageManager,
                            PreferencesManager preferencesManager, JFrame mainFrame) {
        super(mainFrame);

        this.presenter = presenter;
        this.messageManager = messageManager;
        this.preferencesManager = preferencesManager;
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        selectButton.addActionListener(new ActionListener() {
            @Override
            public final void actionPerformed(ActionEvent e) {
                presenter.onSelectMessages();
            }
        });

        markAsUnreadButton.addActionListener(new ActionListener() {
            @Override
            public final void actionPerformed(ActionEvent e) {
                presenter.onMarkMessagesAsUnread();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public final void actionPerformed(ActionEvent e) {
                presenter.onMessagesDelete();
            }
        });

        buttonOK.setText(resourceBundle.getString("messageSmartPart.buttonOkTitle"));
        selectButton.setText(resourceBundle.getString("messageSmartPart.selectButtonTitle"));
        markAsUnreadButton.setText(resourceBundle.getString("messageSmartPart.markAsUnreadButtonTitle"));
        deleteButton.setText(resourceBundle.getString("messageSmartPart.deleteButtonTitle"));
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    @Override
    public final void initialize(UIWorkItem workItem) {
        notNull(workItem, "[this=" + this + ']'); //NON-NLS

        presenter.initialize(workItem, this);

        setTitle(resourceBundle.getString("messageSmartPart.title"));
        setModalityType(APPLICATION_MODAL);

        restoreBounds();

        messageTable.setVisible(true);
        messageTable.setFillsViewportHeight(true);
        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messageTable.setRowSelectionAllowed(true);
        messageTable.setColumnSelectionAllowed(false);
        messageTable.getTableHeader().setReorderingAllowed(false);


        messageTable.setModel(new MessageTableModel(messageManager));

        TableColumn selectedItemColumn = messageTable.getColumnModel().getColumn(0);

        selectedItemColumn.setHeaderValue(""); //NON-NLS
        selectedItemColumn.setPreferredWidth(30);
        selectedItemColumn.setMaxWidth(30);

        TableColumn msgSubjectItemColumn = messageTable.getColumnModel().getColumn(1);

        msgSubjectItemColumn
                .setHeaderValue(resourceBundle.getString("messageSmartPart.messageList.subjectColumnTitle"));

        MessageTableSubjectCellRenderer subjectCellRendered = new MessageTableSubjectCellRenderer();

        msgSubjectItemColumn.setCellRenderer(subjectCellRendered);

        messageTable.getSelectionModel().addListSelectionListener(this);

        //noinspection DuplicateStringLiteralInspection
        messageTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space"); //NON-NLS

        //noinspection DuplicateStringLiteralInspection
        messageTable.getActionMap().put("space", new AbstractAction() { //NON-NLS

            @Override
            public final void actionPerformed(ActionEvent e) {
                int selRowIdx = messageTable.getSelectedRow();

                if (selRowIdx == -1) {
                    return;
                }

                NewsMessage msg = messageManager.getMessages().get(selRowIdx);

                if (selectedMessages.contains(msg)) {
                    selectedMessages.remove(msg);
                } else {
                    selectedMessages.add(msg);
                }

                fireTableDataChanged();

                messageTable.setRowSelectionInterval(selRowIdx, selRowIdx);
            }
        });

        //noinspection DuplicateStringLiteralInspection
        messageTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete"); //NON-NLS

        //noinspection DuplicateStringLiteralInspection
        messageTable.getActionMap().put("delete", new AbstractAction() { //NON-NLS

            @Override
            public final void actionPerformed(ActionEvent e) {
                int selRowIdx = messageTable.getSelectedRow();

                if (selRowIdx == -1) {
                    return;
                }

                NewsMessage msg = messageManager.getMessages().get(selRowIdx);

                presenter.onMessageDelete(msg);
            }
        });

        webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public final void commandReceived(WebBrowserCommandEvent e) {
                if (e.getCommand().equals("openWebPage")) { //NON-NLS
                    try {
                        String url = (String) e.getParameters()[0];

                        Desktop.getDesktop().browse(new URI(url));
                    } catch (Throwable cause) {
                        logger.error("Error during parsing openWebPageCommand.", cause); //NON-NLS
                    }
                }
            }
        });

        presenter.onViewReady();
    }

    @Override
    public final void dispose() {
        if (isDisposed) {
            return;
        }

        isDisposed = true;

        messageTable.getSelectionModel().removeListSelectionListener(this);

        storeBounds();

        presenter.onCloseView();

        presenter.dispose();

        super.dispose();
    }

    @Override
    public final void run() {
        setVisible(true);
    }

    @Override
    public final List<NewsMessage> getSelectedMessages() {
        return new ArrayList<NewsMessage>(selectedMessages);
    }

    @Override
    public final void unselectAllMessages() {
        selectedMessages.clear();

        fireTableDataChanged();
    }

    @Override
    public final void selectAllMessages() {
        selectedMessages.addAll(messageManager.getMessages());

        fireTableDataChanged();
    }

    @Override
    public final NewsMessage getCurrentMessage() {
        int selRow = messageTable.getSelectedRow();

        if (selRow == -1) {
            return null;
        }

        return messageManager.getMessages().get(selRow);
    }

    @Override
    public final void notifyDataChanged() {
        selectedMessages.retainAll(messageManager.getMessages());

        fireTableDataChanged();
    }

    @Override
    public final void setMessageControlsEnabled(boolean enabled) {
        selectButton.setEnabled(enabled);
        markAsUnreadButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    @Override
    public final void setCurrentMessage(NewsMessage msg) {
        if (msg == null) {
            messageTable.clearSelection();
        } else {
            List<NewsMessage> messages = messageManager.getMessages();

            int rowIdx = messages.indexOf(msg);

            if (rowIdx == -1) {
                messageTable.clearSelection();
            } else {
                messageTable.setRowSelectionInterval(rowIdx, rowIdx);
            }
        }
    }

    @Override
    public final void showMessageContent(NewsMessage msg) {
        if (ObjectUtils.equals(currentShowedMsg, msg)) {
            return;
        }

        currentShowedMsg = msg;

        final String contentToSet;

        if (msg == null) {
            contentToSet = "";
        } else {
            contentToSet = msg.getContent();
        }

        if (isEventDispatchThread()) {
            webBrowser.setHTMLContent(contentToSet);
        } else {
            invokeLater(new Runnable() {
                @Override
                public final void run() {
                    webBrowser.setHTMLContent(contentToSet);
                }
            });
        }
    }

    @Override
    public final void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        if (!e.getSource().equals(messageTable.getSelectionModel())) {
            return;
        }

        int rowIdx = messageTable.getSelectedRow();

        if (rowIdx == -1) {
            presenter.onCurrentMessageChanged(null);
        } else {
            messageTable.setColumnSelectionInterval(0, 1);

            NewsMessage msg = messageManager.getMessages().get(rowIdx);

            presenter.onCurrentMessageChanged(msg);
        }
    }

    private void fireTableDataChanged() {
        ((MessageTableModel) messageTable.getModel()).fireTableDataChanged();
    }

    private void createUIComponents() {
        webBrowser = new JWebBrowser();

        webBrowser.setButtonBarVisible(false);
        webBrowser.setDefaultPopupMenuRegistered(false);
        webBrowser.setStatusBarVisible(false);
        webBrowser.setMenuBarVisible(false);
        webBrowser.setLocationBarVisible(false);
        webBrowser.setJavascriptEnabled(true);
        webBrowser.setHTMLContent("");

        deleteButton = new JButton();

        markAsUnreadButton = new JButton();

        selectButton = new JButton();

        buttonOK = new JButton();
    }

    private void restoreBounds() {
        restoreDialogBounds(preferencesManager, MESSAGE_SMART_PART_ID, this, 600, 400);
    }

    private void storeBounds() {
        storeDialogBounds(preferencesManager, MESSAGE_SMART_PART_ID, this);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null,
                        null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(10, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        buttonOK.setText("OK");
        panel2.add(buttonOK,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null,
                        0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0,
                false));
        deleteButton.setText("Delete");
        deleteButton.setMnemonic('D');
        deleteButton.setDisplayedMnemonicIndex(0);
        panel4.add(deleteButton,
                new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 10), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        selectButton.setText("Select/unselect all");
        selectButton.setMnemonic('S');
        selectButton.setDisplayedMnemonicIndex(0);
        panel5.add(selectButton,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 10), -1, -1));
        panel4.add(panel6, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        markAsUnreadButton.setText("Mark as unread");
        markAsUnreadButton.setMnemonic('U');
        markAsUnreadButton.setDisplayedMnemonicIndex(8);
        panel6.add(markAsUnreadButton,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel7, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(70);
        splitPane1.setOrientation(0);
        panel7.add(splitPane1, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null,
                new Dimension(200, 200), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        splitPane1.setLeftComponent(scrollPane1);
        messageTable = new JTable();
        scrollPane1.setViewportView(messageTable);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(panel8);
        panel8.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        panel8.add(webBrowser, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 200),
                null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private class MessageTableSubjectCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
            NewsMessage msg = messageManager.getMessages().get(row);

            if (msg.isUnread()) {
                setForeground(Color.BLACK);
            } else {
                setForeground(Color.GRAY);
            }

            setText(msg.getSubject());

            if (isSelected) {
                setOpaque(true);
                setBackground(messageTable.getSelectionBackground());
            } else {
                setOpaque(false);
                setBackground(messageTable.getBackground());
            }

            return this;
        }
    }

    private class MessageTableModel extends AbstractTableModel {
        private final MessageManager messageManager;

        public MessageTableModel(MessageManager messageManager) {
            this.messageManager = messageManager;
        }

        @Override
        public final int getRowCount() {
            return messageManager.getMessages().size();
        }

        @Override
        public final int getColumnCount() {
            return 2;
        }

        @Override
        public final Object getValueAt(int rowIndex, int columnIndex) {
            NewsMessage msg = messageManager.getMessages().get(rowIndex);

            if (columnIndex == 0) {
                return selectedMessages.contains(msg);
            } else { // columnIndex == 1
                return msg.getSubject();
            }
        }

        @Override
        public final Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            } else { // columnIndex == 1
                return String.class;
            }
        }

        @Override
        public final boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public final void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex != 0) {
                return;
            }

            Boolean value = (Boolean) aValue;

            NewsMessage msg = messageManager.getMessages().get(rowIndex);

            if (value) {
                selectedMessages.add(msg);
            } else {
                selectedMessages.remove(msg);
            }
        }
    }
}
