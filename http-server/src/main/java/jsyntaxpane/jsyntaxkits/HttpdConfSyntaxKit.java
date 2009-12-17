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
