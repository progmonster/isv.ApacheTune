package com.apachetune.core.ui.editors;

import com.apachetune.core.utils.StringValue;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface SaveFilesHelperCallBack {
    void prepareSaveAllFiles(StringValue title, StringValue message);

    void prepareSaveFile(Object fileId, StringValue title, StringValue message);

    void saveFile(Object fileId);

    boolean isFileDirty(Object fileId);
}
