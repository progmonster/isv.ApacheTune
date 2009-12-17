package com.apachetune.core.ui.impl;

import com.apachetune.core.ui.*;
import static org.apache.commons.lang.StringUtils.*;

import javax.swing.text.*;
import static javax.swing.text.StyleConstants.*;
import java.awt.*;
import static java.awt.Color.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class OutputPaneDocumentImpl extends DefaultStyledDocument implements OutputPaneDocument {
    private static final long serialVersionUID = -8914774787481869235L;

    public void clear() {
        setText(null);
    }

    public void setText(String text) {
        setColoredText(text, BLACK);
    }

    public void setColoredText(String text, Color color) {
        if (color == null) {
            throw new NullPointerException("Argument color cannot be a null [this = " + this + "]");
        }

        MutableAttributeSet attr = new SimpleAttributeSet();

        setForeground(attr, color);

        try {
            replace(0, getLength(), defaultString(text), attr);
        } catch (BadLocationException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }
}
