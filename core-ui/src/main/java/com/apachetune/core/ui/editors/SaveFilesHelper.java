package com.apachetune.core.ui.editors;

import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface SaveFilesHelper {
    void initialize(Collection filesIds, SaveFilesHelperCallBack helperCallBack);

    boolean execute();
}
