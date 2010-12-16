package com.apachetune.core.errorreportsystem;

import javax.swing.*;
import java.awt.*;

public class ErrorReportDialog extends JDialog {
    private JPanel contentPane;
    private JTextArea pleaseWaitWhileErrorTextArea;

    public ErrorReportDialog(Component parentComponent) {
        setContentPane(contentPane);

        setAlwaysOnTop(true);
        setJMenuBar(null);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setTitle(null);
        setUndecorated(true);

        setSize(400, 60);
        setLocationRelativeTo(parentComponent);

        setModal(true);
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
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        contentPane.add(panel1, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, BorderLayout.WEST);
        final JLabel label1 = new JLabel();
        label1.setIcon(
                new ImageIcon(getClass().getResource("/com/apachetune/core/errorreportsystem/send_error_report.gif")));
        label1.setText("");
        panel2.add(label1, BorderLayout.CENTER);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel1.add(panel3, BorderLayout.CENTER);
        pleaseWaitWhileErrorTextArea = new JTextArea();
        pleaseWaitWhileErrorTextArea.setEditable(false);
        pleaseWaitWhileErrorTextArea.setOpaque(false);
        pleaseWaitWhileErrorTextArea
                .setText("Please, wait while error report and application\nlog will be sent to developer team...");
        panel3.add(pleaseWaitWhileErrorTextArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
