package com.apachetune.core.ui;

import javax.swing.*;
import java.io.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface ResourceLocator {
    ImageIcon loadIcon(String name) throws IOException;
}
