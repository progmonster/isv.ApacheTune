package com.apachetune.httpserver.ui.selectserver;

import com.apachetune.core.ResourceManager;
import com.apachetune.core.WorkItem;
import com.apachetune.core.ui.SmartPart;
import com.google.inject.Inject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.l2fprod.common.swing.JDirectoryChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;

import static org.apache.commons.lang.Validate.notNull;

public class SelectServerSmartPart extends JDialog implements SmartPart, SelectServerDialog {
    public final SelectServerPresenter presenter;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JDirectoryChooser directoryChooser;
    private JLabel descriptionLabel;
    private JTextPane httpServerVersionWarningLabel;

    @SuppressWarnings({"FieldCanBeLocal"})
    private final ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(SelectServerSmartPart.class);

    @Inject
    public SelectServerSmartPart(final SelectServerPresenter presenter, JFrame mainFrame) {
        super(mainFrame);

        //noinspection DuplicateStringLiteralInspection
        notNull(presenter, "Argument presenter cannot be a null"); //NON-NLS

        this.presenter = presenter;

        setContentPane(contentPane);
        setModal(true);
        setTitle(resourceBundle.getString("selectServerSmartPart.title"));
        getRootPane().setDefaultButton(buttonOK);

        pack();

        setLocationRelativeTo(mainFrame);


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }
        );

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }
        );

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        }
        );

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        directoryChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String prop = evt.getPropertyName();
                if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
                    File file = (File) evt.getNewValue();

                    presenter.onCurrentDirectoryChanged(file);
                }
            }
        }
        );

        directoryChooser.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        directoryChooser.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        httpServerVersionWarningLabel
                .setText(resourceBundle.getString("selectServerSmartPart.httpServerVersionWarningLabel"));
        buttonOK.setText(resourceBundle.getString("selectServerSmartPart.buttonOK.title"));
        buttonCancel.setText(resourceBundle.getString("selectServerSmartPart.buttonCancel.title"));
        descriptionLabel.setText(resourceBundle.getString("selectServerSmartPart.descriptionLabel"));
    }

    public void initialize(WorkItem workItem) {
        //noinspection DuplicateStringLiteralInspection
        notNull(workItem, "Argument workItem cannot be a null"); //NON-NLS

        presenter.initialize(workItem, this);
    }

    public void run() {
        setVisible(true);
    }

    public void setCurrentDir(String currentDir) {
        notNull(currentDir, "Argument currentDir cannot be a null"); //NON-NLS

        directoryChooser.setSelectedFile(new File(currentDir));
    }

    public String getPath() {
        return directoryChooser.getSelectedFile().getAbsolutePath();
    }

    public void setCurrentDirectorySelectable(boolean isSelectable) {
        buttonOK.setEnabled(isSelectable);
    }

    private void onOK() {
        dispose();

        presenter.onDirectorySelected();
    }

    private void onCancel() {
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMinimumSize(new Dimension(328, 392));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1,
                new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null,
                        null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        buttonOK = new JButton();
        buttonOK.setEnabled(false);
        buttonOK.setText("Select");
        panel2.add(buttonOK,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel,
                new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null,
                        0, false));
        descriptionLabel = new JLabel();
        descriptionLabel.setText("Select an Apache HTTP-server home directory.");
        panel3.add(descriptionLabel,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false));
        directoryChooser = new JDirectoryChooser();
        directoryChooser.setControlButtonsAreShown(false);
        directoryChooser.setShowingCreateDirectory(false);
        panel3.add(directoryChooser,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null,
                        new Dimension(328, 392), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null,
                        0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 5), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(
                getClass().getResource("/com/apachetune/httpserver/ui/selectserver/strict_support_warning.png")));
        label1.setText("");
        label1.setVerticalAlignment(0);
        label1.setVerticalTextPosition(3);
        panel5.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        httpServerVersionWarningLabel = new JTextPane();
        httpServerVersionWarningLabel.setEditable(false);
        httpServerVersionWarningLabel.setOpaque(false);
        httpServerVersionWarningLabel.setText(
                "Warning! This application was tested on Apache HTTP Server version 2.2. only.\nUsing with another version is on your own risk.");
        panel5.add(httpServerVersionWarningLabel,
                new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null,
                        0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
