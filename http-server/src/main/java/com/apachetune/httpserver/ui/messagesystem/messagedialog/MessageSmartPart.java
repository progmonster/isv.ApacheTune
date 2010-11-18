package com.apachetune.httpserver.ui.messagesystem.messagedialog;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.ui.NSmartPart;
import com.apachetune.core.ui.UIWorkItem;
import com.apachetune.httpserver.ui.messagesystem.MessageManager;
import com.apachetune.httpserver.ui.messagesystem.MessageStore;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.google.inject.Inject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;

import static java.awt.Dialog.ModalityType.TOOLKIT_MODAL;
import static org.apache.commons.lang.Validate.notNull;

public class MessageSmartPart extends JDialog implements NSmartPart, MessageView, ListSelectionListener {
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

    @Inject
    public MessageSmartPart(final MessagePresenter presenter, MessageManager messageManager,
                            PreferencesManager preferencesManager, JFrame mainFrame) {
        super(mainFrame);
        
        this.presenter = presenter;
        this.messageManager = messageManager;
        this.preferencesManager = preferencesManager;
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
        notNull(workItem, "[this=" + this + ']');

        presenter.initialize(workItem, this);

        setTitle("News Messages"); // todo localize
        setModalityType(TOOLKIT_MODAL);

        restoreBounds();

        messageTable.setVisible(true);
        messageTable.setFillsViewportHeight(true);
        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messageTable.setRowSelectionAllowed(true);
        messageTable.setColumnSelectionAllowed(false);
        messageTable.getTableHeader().setReorderingAllowed(false);


        messageTable.setModel(new MessageTableModel(messageManager));

        TableColumn selectedItemColumn = messageTable.getColumnModel().getColumn(0);

        selectedItemColumn.setHeaderValue("");
        selectedItemColumn.setPreferredWidth(30);
        selectedItemColumn.setMaxWidth(30);

        TableColumn msgSubjectItemColumn = messageTable.getColumnModel().getColumn(1);

        msgSubjectItemColumn.setHeaderValue("Message Subject");

        MessageTableSubjectCellRenderer subjectCellRendered = new MessageTableSubjectCellRenderer();

        msgSubjectItemColumn.setCellRenderer(subjectCellRendered);

        messageTable.getSelectionModel().addListSelectionListener(this);

        messageTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space");

        messageTable.getActionMap().put("space", new AbstractAction() {
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

        messageTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");

        messageTable.getActionMap().put("delete", new AbstractAction() {
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

        presenter.onViewReady();
    }

    @Override
    public final void close() {
        messageTable.getSelectionModel().removeListSelectionListener(this);
        
        storeBounds();

        presenter.onCloseView();

        presenter.dispose();
    }

    @Override
    public final void run() {
        setVisible(true);
    }

    @Override
    public final List<NewsMessage> getSelectedMessages() {
        return new ArrayList(selectedMessages);
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

        webBrowser.navigate("http://habrahabr.ru/"); // todo remove
    }

    private void restoreBounds() {
        setMinimumSize(new Dimension(600, 400));

        Preferences pref = preferencesManager.userNodeForPackage(MessageSmartPart.class);

        int left = pref.getInt("left", Integer.MAX_VALUE);

        int top = pref.getInt("top", Integer.MAX_VALUE);

        int width = pref.getInt("width", 600);

        int height = pref.getInt("height", 400);

        setSize(width, height);

        if ((left == Integer.MAX_VALUE) || (top == Integer.MAX_VALUE)) {
            setLocationRelativeTo(null);
        } else {
            setLocation(left, top);
        }
    }

    private void storeBounds() {
        Preferences pref = preferencesManager.userNodeForPackage(MessageSmartPart.class);

        pref.putInt("left", getLocation().x);
        pref.putInt("top", getLocation().y);
        pref.putInt("width", getSize().width);
        pref.putInt("height", getSize().height);

        try {
            pref.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Internal error.", e);
        }
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
