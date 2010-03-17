package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.EDIT_REDO_ACTION;
import static com.apachetune.core.ui.Constants.EDIT_UNDO_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface UndoWorkflowActionSite extends ActionSite {
    @ActionHandler(EDIT_UNDO_ACTION)
    void onUndo();

    @ActionPermission(EDIT_UNDO_ACTION)
    boolean isUndoEnabled();

    @ActionHandler(EDIT_REDO_ACTION)
    void onRedo();

    @ActionPermission(EDIT_REDO_ACTION)
    boolean isRedoEnabled();
}
