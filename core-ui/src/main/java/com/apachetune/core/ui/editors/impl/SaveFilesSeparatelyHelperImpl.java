package com.apachetune.core.ui.editors.impl;

import com.apachetune.core.ResourceManager;
import com.apachetune.core.ui.editors.SaveFilesHelper;
import com.apachetune.core.ui.editors.SaveFilesHelperCallBack;
import com.apachetune.core.utils.StringValue;
import com.google.inject.Inject;
import org.apache.commons.collections.Predicate;

import javax.swing.*;
import java.util.Collection;
import java.util.ResourceBundle;

import static javax.swing.JOptionPane.*;
import static org.apache.commons.collections.CollectionUtils.exists;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class SaveFilesSeparatelyHelperImpl implements SaveFilesHelper {
    private final JFrame mainFrame;

    private Collection fileIds;

    private SaveFilesHelperCallBack helperCallBack;

    private ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(SaveFilesSeparatelyHelperImpl.class);

    @Inject
    public SaveFilesSeparatelyHelperImpl(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void initialize(Collection fileIds, SaveFilesHelperCallBack helperCallBack) {
        //noinspection DuplicateStringLiteralInspection
        notNull(fileIds, "Argument fileIds cannot be a null"); //NON-NLS

        //noinspection DuplicateStringLiteralInspection
        notNull(helperCallBack, "Argument helperCallBack cannot be a null"); //NON-NLS

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
            boolean needRequest = true;

            boolean isCancelled = false;

            for (Object fileId : fileIds) {
                if (!helperCallBack.isFileDirty(fileId)) {
                    continue;
                }

                StringValue title = new StringValue();

                StringValue message = new StringValue();

                helperCallBack.prepareSaveFile(fileId, title, message);

                if (needRequest) {
                    int result = showOptionDialog(mainFrame, message.value, title.value, DEFAULT_OPTION,
                            QUESTION_MESSAGE, null, new String[] {
                                    resourceBundle.getString(
                                            "saveFilesSeparatelyHelperImpl.execute.showOptionDialog.saveOption"),
                                    resourceBundle.getString(
                                            "saveFilesSeparatelyHelperImpl.execute.showOptionDialog.saveAllOption"),
                                    resourceBundle.getString(
                                            "saveFilesSeparatelyHelperImpl.execute.showOptionDialog.cancelOption"),
                                    resourceBundle.getString(
                                            "saveFilesSeparatelyHelperImpl.execute.showOptionDialog.dontSaveOption")
                            }, null);

                    if ((result == CLOSED_OPTION) || (result == 2 /* Cancel */)) {
                        isCancelled = true;

                        break;
                    } else if (result == 1) { // Save all
                        needRequest = false;
                    } else if (result == 3) { // Don't save
                       continue;
                    }
                }

                helperCallBack.saveFile(fileId);
            }

            return !isCancelled;
        } else {
            return true;
        }
    }
}
