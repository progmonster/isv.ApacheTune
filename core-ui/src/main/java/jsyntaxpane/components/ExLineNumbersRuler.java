package jsyntaxpane.components;

import javax.swing.*;
import java.awt.event.MouseListener;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ExLineNumbersRuler extends LineNumbersRuler {
    @Override
    public void install(JEditorPane editor) {
        super.install(editor);

        // PATCH to avoid a "Go to line" dialog showing.
        for (MouseListener listener : getMouseListeners()) {
            removeMouseListener(listener);
        }
    }
}
