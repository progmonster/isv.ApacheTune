package com.apachetune.core.ui.editors;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface EditorManager {
    EditorWorkItem createEditorWorkItem(EditorInput editorInput);

    void saveAllEditorsContent();
}
