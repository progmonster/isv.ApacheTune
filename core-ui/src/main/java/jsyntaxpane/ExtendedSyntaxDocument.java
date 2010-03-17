package jsyntaxpane;

import javax.swing.undo.UndoManager;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ExtendedSyntaxDocument extends SyntaxDocument {
    private static final long serialVersionUID = 6628710493389109673L;

    public ExtendedSyntaxDocument(Lexer lexer) {
        super(lexer);
    }

    public UndoManager getUndoManager() {
        return undo;
    }
}
