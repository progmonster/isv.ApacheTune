package com.apachetune.core.ui.editors.impl;

import com.apachetune.core.ui.editors.SaveFilesHelper;
import com.apachetune.core.ui.editors.SaveFilesHelperCallBack;
import com.apachetune.core.utils.StringValue;
import com.google.inject.Inject;
import org.apache.commons.collections.Predicate;

import javax.swing.*;
import java.util.Collection;

import static javax.swing.JOptionPane.*;
import static org.apache.commons.collections.CollectionUtils.exists;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class SaveAllFilesAtOnceHelperImpl implements SaveFilesHelper {
    private final JFrame mainFrame;

    private Collection fileIds;

    private SaveFilesHelperCallBack helperCallBack;

    @Inject
    public SaveAllFilesAtOnceHelperImpl(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void initialize(Collection fileIds, SaveFilesHelperCallBack helperCallBack) {
        if (fileIds == null) {
            throw new NullPointerException("Argument fileIds cannot be a null [this = " + this + "]");
        }

        if (helperCallBack == null) {
            throw new NullPointerException("Argument helperCallBack cannot be a null [this = " + this + "]");
        }

        this.fileIds = fileIds;
        this.helperCallBack = helperCallBack;
    }

    public boolean execute() {
        boolean isDirtyFilesExist = exists(fileIds, new Predicate() {
            public boolean evaluate(Object object) {
                return helperCallBack.isFileDirty(object); 
            }
        });

        if (isDirtyFilesExist) {
            StringValue title = new StringValue();

            StringValue message = new StringValue();

            helperCallBack.prepareSaveAllFiles(title, message);

            int result = showConfirmDialog(
                    mainFrame, message.value, title.value, OK_CANCEL_OPTION, QUESTION_MESSAGE);

            if (result == OK_OPTION) {
                for (Object fileId : fileIds) {
                    if (helperCallBack.isFileDirty(fileId)) {
                        helperCallBack.saveFile(fileId);
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return true; // No dirty files exist, no user selection needed. Will enable to continue a process.
        }
    }
}
