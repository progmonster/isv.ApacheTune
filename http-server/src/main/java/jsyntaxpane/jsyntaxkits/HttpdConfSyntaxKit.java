package jsyntaxpane.jsyntaxkits;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.ExtendedSyntaxDocument;
import jsyntaxpane.lexers.HttpdConfLexer;

import javax.swing.text.Document;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class HttpdConfSyntaxKit extends DefaultSyntaxKit {
    private static final long serialVersionUID = -346460681502494915L;

    public HttpdConfSyntaxKit() {
        super(new HttpdConfLexer());
    }

    @Override
    public Document createDefaultDocument() {
        return new ExtendedSyntaxDocument(new HttpdConfLexer());
    }
}
