package com.apachetune.core.ui.editors;

import com.apachetune.core.ui.UIWorkItem;
import jsyntaxpane.ExtendedSyntaxDocument;

import java.awt.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface EditorWorkItem extends UIWorkItem {
    void setEditorInput(EditorInput editorInput);

    boolean isDirty();

    void save();

    ExtendedSyntaxDocument getDocument();

    int getCaretPosition();

    void setCaretPosition(int position);

    int getLineStartPosition(int lineNum);

    void highlightLine(int lineNum, Color red);
}
