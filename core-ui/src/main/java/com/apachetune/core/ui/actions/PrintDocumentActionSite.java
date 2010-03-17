package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.FILE_PRINT_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface PrintDocumentActionSite extends ActionSite {
    @ActionHandler(FILE_PRINT_ACTION)
    void onPrintDocument();

    @ActionPermission(FILE_PRINT_ACTION)
    boolean isPrintDocumentEnabled();
}
