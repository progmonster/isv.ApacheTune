package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.core.ResourceManager;
import com.apachetune.httpserver.ui.updating.UpdateConfiguration;
import com.apachetune.httpserver.ui.updating.UpdateException;
import com.apachetune.httpserver.ui.updating.UpdateInfo;
import com.apachetune.httpserver.ui.updating.UpdateInfoDialog;
import com.google.inject.Inject;
import org.apache.commons.lang.mutable.MutableBoolean;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import static java.text.MessageFormat.format;
import static javax.swing.JOptionPane.*;

/**
 * FIXDOC
 */
public class UpdateInfoDialogImpl implements UpdateInfoDialog {
    private final JFrame mainFrame;

    private final JCheckBox checkForUpdateEnabledChk = new JCheckBox();

    private final ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(UpdateInfoDialogImpl.class);

    @Inject
    public UpdateInfoDialogImpl(JFrame mainFrame, UpdateConfiguration updateConfiguration) {
        this.mainFrame = mainFrame;

        checkForUpdateEnabledChk.setSelected(updateConfiguration.getCheckUpdateFlag());
        checkForUpdateEnabledChk.setText(resourceBundle.getString("updateInfoDialogImpl.checkForUpdateOnStartTitle"));
    }

    @Override
    public final UserActionOnUpdate showHasUpdate(UpdateInfo updateInfo) {
        final Object[] inputs = new Object[] {
                format(resourceBundle.getString("updateInfoDialogImpl.hasUpdateDialog.message"),
                        updateInfo.getUserFriendlyFullAppName()),
                checkForUpdateEnabledChk
        };

        boolean isUserAgreeUpdate = (showConfirmDialog(mainFrame, inputs,
                resourceBundle.getString("updateInfoDialogImpl.hasUpdateDialog.title"),
                YES_NO_OPTION) ==
                YES_OPTION);

        return new UserActionOnUpdate(isUserAgreeUpdate, checkForUpdateEnabledChk.isSelected());
    }

    @Override
    public final UserActionOnNoUpdate showHasNoUpdate() {
        final Object[] inputs = new Object[] {
                resourceBundle.getString("updateInfoDialogImpl.hasNoUpdateDialog.message"),
                checkForUpdateEnabledChk
        };

        showMessageDialog(mainFrame, inputs,
                resourceBundle.getString("updateInfoDialogImpl.hasNoUpdateDialog.title"),
                INFORMATION_MESSAGE);

        return new UserActionOnNoUpdate(checkForUpdateEnabledChk.isSelected());
    }

    @Override
    public final UserActionOnUpdateError showUpdateCheckingError(UpdateException e) {
        JOptionPane optionPane = new JOptionPane(
                format(resourceBundle.getString("updateInfoDialogImpl.errorUpdateCheckingDialog.message"),
                       e.getMessage()),
                ERROR_MESSAGE);

        final MutableBoolean isUserAgreeSendErrorReport = new MutableBoolean();

        JButton sendReportBtn = new JButton(
                resourceBundle.getString("updateInfoDialogImpl.errorUpdateCheckingDialog.sendErrorReportButton.title")
        );

        JButton cancelBtn = new JButton(
                resourceBundle.getString("updateInfoDialogImpl.errorUpdateCheckingDialog.cancelButton.title")
        );

        optionPane.setOptions(new Object[] {sendReportBtn, cancelBtn});

        final JDialog dialog = optionPane.createDialog(mainFrame,
                resourceBundle.getString("updateInfoDialogImpl.errorUpdateCheckingDialog.title")
        );

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
