package com.apachetune.core.ui.editors;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface EditorManager {
    EditorWorkItem createEditorWorkItem(EditorInput editorInput);

    void saveAllEditorsContent();

/*
    void addEditorListener(EditorListener listener);

    void removeEditorListener(EditorListener listener);

    void removeAllEditorListeners();
*/
}
