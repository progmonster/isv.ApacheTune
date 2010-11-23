package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.httpserver.ui.updating.HasUpdateMessageDialog;
import com.apachetune.httpserver.ui.updating.UpdateConfiguration;
import com.apachetune.httpserver.ui.updating.UpdateInfo;
import com.google.inject.Inject;

import javax.swing.*;

import static java.text.MessageFormat.format;
import static javax.swing.JOptionPane.*;

/**
 * FIXDOC
 */
public class HasUpdateMessageDialogImpl implements HasUpdateMessageDialog {
    private final JFrame mainFrame;

    private final UpdateConfiguration updateConfiguration;

    private final JCheckBox checkForUpdateEnabledChk = new JCheckBox();

    @Inject
    public HasUpdateMessageDialogImpl(JFrame mainFrame, UpdateConfiguration updateConfiguration) {
        this.mainFrame = mainFrame;
        this.updateConfiguration = updateConfiguration;
    }

    @Override
    public final UpdateAction show(UpdateInfo updateInfo) {
        checkForUpdateEnabledChk.setSelected(updateConfiguration.getCheckUpdateFlag());
        checkForUpdateEnabledChk.setText("check for updates on application start"); // todo localize

        final Object[] inputs = new Object[] {
                format("There are update for this application - {0}.\nDo you want to update it?",  // todo localize
                        updateInfo.getUserFriendlyFullAppName()),
                checkForUpdateEnabledChk
        };

        if (showConfirmDialog(mainFrame, inputs, "Update application", YES_NO_OPTION) == YES_OPTION) { // todo localize
            return UpdateAction.NEED_UPDATE;
        } else {
            return UpdateAction.SKIP_UPDATE;
        }
    }

    @Override
    public final boolean isUserEnableCheckForUpdate() {
        return checkForUpdateEnabledChk.isSelected();
    }
}
