package com.apachetune.httpserver.ui.messagesystem.messagedialog;

import com.apachetune.core.ui.NSmartPart;
import com.apachetune.core.ui.UIWorkItem;
import com.google.inject.Inject;

import javax.swing.*;
import java.awt.event.*;

import static org.apache.commons.lang.Validate.notNull;

public class MessageSmartPart extends JDialog implements NSmartPart, MessageView {
    private final MessagePresenter presenter;

    private JPanel contentPane;
    private JButton buttonOK;    
    private JButton buttonCancel;

    @Inject
    public MessageSmartPart(MessagePresenter presenter) {
        this.presenter = presenter;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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
    public void initialize(UIWorkItem workItem) {
        notNull(workItem, "[this=" + this + ']');

        presenter.initialize(workItem, this);

        // TODO implement
    }

    @Override
    public void close() {
        // TODO implement
    }
}
