package com.apachetune.core.ui.editors;

import java.util.Collection;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface SaveFilesHelper {
    void initialize(Collection filesIds, SaveFilesHelperCallBack helperCallBack);

    boolean execute();
}
