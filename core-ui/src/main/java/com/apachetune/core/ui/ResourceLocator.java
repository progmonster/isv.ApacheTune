package com.apachetune.core.ui;

import javax.swing.*;
import java.io.IOException;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface ResourceLocator {
    ImageIcon loadIcon(String name) throws IOException;
}
