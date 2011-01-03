package com.apachetune.core;

import java.io.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static com.apachetune.core.utils.Utils.createRuntimeException;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 */
public class ResourceManager {
    private static final ResourceManager INSTANCE = new ResourceManager();

    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    public final ResourceBundle getResourceBundle(Class clazz) {
        notNull(clazz);

        try {
            final InputStream is = clazz.getResourceAsStream("messages.properties"); //NON-NLS

            notNull(is);

            Reader reader = new InputStreamReader(is, "UTF-8"); //NON-NLS

            return new PropertyResourceBundle(reader);
        } catch (UnsupportedEncodingException e) {
            throw createRuntimeException(e);
        } catch (IOException e) {
            throw createRuntimeException(e);
        }
    }
}
