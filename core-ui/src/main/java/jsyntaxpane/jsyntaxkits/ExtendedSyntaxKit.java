package jsyntaxpane.jsyntaxkits;

import jsyntaxpane.*;
import jsyntaxpane.lexers.*;

import javax.swing.text.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
