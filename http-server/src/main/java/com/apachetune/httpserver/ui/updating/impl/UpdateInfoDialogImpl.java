package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.httpserver.ui.updating.UpdateConfiguration;
import com.apachetune.httpserver.ui.updating.UpdateException;
import com.apachetune.httpserver.ui.updating.UpdateInfo;
import com.apachetune.httpserver.ui.updating.UpdateInfoDialog;
import com.google.inject.Inject;
import org.apache.commons.lang.mutable.MutableBoolean;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.text.MessageFormat.format;
import static javax.swing.JOptionPane.*;

/**
 * FIXDOC
 */
public class UpdateInfoDialogImpl implements UpdateInfoDialog {
    private final JFrame mainFrame;

    private final JCheckBox checkForUpdateEnabledChk = new JCheckBox();

    @Inject
    public UpdateInfoDialogImpl(JFrame mainFrame, UpdateConfiguration updateConfiguration) {
        this.mainFrame = mainFrame;

        checkForUpdateEnabledChk.setSelected(updateConfiguration.getCheckUpdateFlag());
        checkForUpdateEnabledChk.setText("check for updates on application start"); // todo localize
    }

    @Override
    public final UserActionOnUpdate showHasUpdate(UpdateInfo updateInfo) {
        final Object[] inputs = new Object[] {
                format("There is update for this application - {0}.\nDo you want to update it?",  // todo localize
                        updateInfo.getUserFriendlyFullAppName()),
                checkForUpdateEnabledChk
        };

        boolean isUserAgreeUpdate = (showConfirmDialog(mainFrame, inputs, "Update application", YES_NO_OPTION) ==
                YES_OPTION);  // todo localize

        return new UserActionOnUpdate(isUserAgreeUpdate, checkForUpdateEnabledChk.isSelected());
    }

    @Override
    public final UserActionOnNoUpdate showHasNoUpdate() {
        final Object[] inputs = new Object[] {
                "There is no update for this application.",  // todo localize
                checkForUpdateEnabledChk
        };

        showMessageDialog(mainFrame, inputs, "Update application", INFORMATION_MESSAGE);  // todo localize

        return new UserActionOnNoUpdate(checkForUpdateEnabledChk.isSelected());
    }

    @Override
    public final UserActionOnUpdateError showUpdateCheckingError(UpdateException e) {
        JOptionPane optionPane = new JOptionPane(
                format("An error occurred during checking application update [{0}].\n\n" +
                    "It may be temporary internet connection problem, but if this error repeats,\n" +
                    "please, send error report to application developers.", e.getMessage()),
                ERROR_MESSAGE);

        final MutableBoolean isUserAgreeSendErrorReport = new MutableBoolean();

        JButton sendReportBtn = new JButton("Send error report");

        JButton cancelBtn = new JButton("Cancel");

        optionPane.setOptions(new Object[] {sendReportBtn, cancelBtn});

        final JDialog dialog = optionPane.createDialog(mainFrame, "Update application");

        sendReportBtn.addActionListener(new ActionListener() {
            @Override
            public final void actionPerformed(ActionEvent e) {
                isUserAgreeSendErrorReport.setValue(true);

                dialog.setVisible(false);
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public final void actionPerformed(ActionEvent e) {
                isUserAgreeSendErrorReport.setValue(false);

                dialog.setVisible(false);
            }
        });

        dialog.setVisible(true);

        return new UserActionOnUpdateError(isUserAgreeSendErrorReport.booleanValue());
    }
}
