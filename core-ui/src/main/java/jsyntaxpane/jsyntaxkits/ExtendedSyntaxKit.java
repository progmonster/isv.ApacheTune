package jsyntaxpane.jsyntaxkits;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.ExtendedSyntaxDocument;
import jsyntaxpane.lexers.ExtendedLexer;

import javax.swing.text.Document;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ExtendedSyntaxKit extends DefaultSyntaxKit {
    private static final long serialVersionUID = 490682544249077692L;

    public ExtendedSyntaxKit() {
        super(new ExtendedLexer());
    }

    @Override
    public Document createDefaultDocument() {
        return new ExtendedSyntaxDocument(new ExtendedLexer());
    }
}
