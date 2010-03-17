package com.apachetune.core.ui;

import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public abstract class GenericResourceLocator implements ResourceLocator {
    public final ImageIcon loadIcon(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("Argument name cannot be a null");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Argument name cannot be empty");
        }

        return new ImageIcon(IOUtils.toByteArray(getClass().getResourceAsStream(name)));
    }
}
