package com.apachetune.httpserver.ui.editors;

import com.apachetune.core.ui.editors.EditorInput;
import com.apachetune.httpserver.entities.ServerObjectInfo;
import com.apachetune.httpserver.ui.resources.HttpServerResourceLocator;
import com.google.inject.Inject;
import jsyntaxpane.jsyntaxkits.HttpdConfSyntaxKit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.net.URI;

import static com.apachetune.core.utils.Utils.createRuntimeException;
import static com.apachetune.httpserver.Constants.EDITOR_WORK_ITEM;
import static com.apachetune.httpserver.Constants.TEXT_HTTPDCONF_CONTENT_TYPE;
import static java.io.File.separatorChar;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static jsyntaxpane.DefaultSyntaxKit.getContentTypes;
import static jsyntaxpane.DefaultSyntaxKit.registerContentType;
import static org.apache.commons.lang.ArrayUtils.contains;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ConfEditorInput implements EditorInput {
    Logger logger = LoggerFactory.getLogger(ConfEditorInput.class);    

    private final HttpServerResourceLocator httpServerResourceLocator;

    private final JFrame mainFrame;

    private ServerObjectInfo serverObjectInfo;

    @Inject
    public ConfEditorInput(HttpServerResourceLocator httpServerResourceLocator, JFrame mainFrame) {
        this.httpServerResourceLocator = httpServerResourceLocator;
        this.mainFrame = mainFrame;
    }

    public void setData(ServerObjectInfo serverObjectInfo) {
        notNull(serverObjectInfo, "Argument serverObjectInfo cannot be a null");

        this.serverObjectInfo = serverObjectInfo;
    }

    public URI getDocumentUri() {
        return serverObjectInfo.getLocation().toURI();
    }

    public String getWorkItemId() {
        return EDITOR_WORK_ITEM + "::" + getLocation().getAbsolutePath();
    }

    public String getContentPaneTitle() {
        return serverObjectInfo.getTitle();
    }

    public String getContentPaneId() {
        return getWorkItemId();
    }

    public String getPrintTitle() {
        return StringUtils.abbreviate(getLocation().getParentFile().getAbsolutePath(), 20) + separatorChar +
                getLocation().getName();
    }

    public String getSaveTitle() {
        return getLocation().getAbsolutePath();
    }

    public String getContentType() {
        registerMainConfContentType();

        return TEXT_HTTPDCONF_CONTENT_TYPE;
    }

    public String loadContent() {
        try {
            // TODO This file may be placed in another places. Make ability of searching this file inside a server root.
            // TODO Check for file encoding (What kinds of encodings can be)

            if (getLocation().exists()) {
                return IOUtils.toString(new BufferedInputStream(new FileInputStream(getLocation())));
            } else {
                return "";
            }
        } catch (FileNotFoundException e) {
            logger.error("File not found.", e);

            // todo localize
            showMessageDialog(mainFrame, "Cannot load file " + getLocation().getAbsolutePath(), "Error", ERROR_MESSAGE);

            return "";
        } catch (IOException e) {
            throw createRuntimeException(e);
        }
    }

    public void saveContent(String content) {
        notNull(content, "Argument content cannot be a null");

        try {
            // TODO Hardcoded block. Remove it.
            if (!getLocation().getParentFile().exists()) {
                //noinspection ResultOfMethodCallIgnored
                getLocation().getParentFile().mkdir();
            }

            OutputStream os = new BufferedOutputStream(new FileOutputStream(getLocation()));

            // TODO check about main conf file encoding.
            IOUtils.write(content, os, "UTF-8");

            os.close();
        } catch (FileNotFoundException e) {
            logger.error("File not found.", e);

            // todo localize
            showMessageDialog(mainFrame, "Cannot save file " + getLocation().getAbsolutePath(), "Error", ERROR_MESSAGE);
        } catch (IOException e) {
            throw createRuntimeException(e);
        }
    }

    public Icon getContentPaneIcon() {
        try {
            return httpServerResourceLocator.loadIcon("config_file_icon.png");
        } catch (IOException e) {
            throw createRuntimeException(e);
        }
    }

    private void registerMainConfContentType() {
        String syntaxKitClassName = HttpdConfSyntaxKit.class.getName();

        String[] contentTypes = getContentTypes();

        if (!contains(contentTypes, TEXT_HTTPDCONF_CONTENT_TYPE)) {
            registerContentType(TEXT_HTTPDCONF_CONTENT_TYPE, syntaxKitClassName);
        }
    }

    private File getLocation() {
        return serverObjectInfo.getLocation();
    }
}
