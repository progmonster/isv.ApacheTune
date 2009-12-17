package com.apachetune.core.ui;

import javax.swing.text.*;
import java.awt.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface OutputPaneDocument extends Document {
    void clear();

    void setText(String text);

    void setColoredText(String text, Color color);
}
