package com.apachetune.httpserver.ui.editors;

import com.apachetune.core.ResourceManager;
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
import java.util.ResourceBundle;

import static com.apachetune.core.utils.Utils.createRuntimeException;
import static com.apachetune.httpserver.Constants.EDITOR_WORK_ITEM;
import static com.apachetune.httpserver.Constants.TEXT_HTTPDCONF_CONTENT_TYPE;
import static java.io.File.separatorChar;
import static java.text.MessageFormat.format;
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

    private ResourceBundle resourceBundle = ResourceManager.getInstance().getResourceBundle(ConfEditorInput.class);

    @Inject
    public ConfEditorInput(HttpServerResourceLocator httpServerResourceLocator, JFrame mainFrame) {
        this.httpServerResourceLocator = httpServerResourceLocator;
        this.mainFrame = mainFrame;
    }

    public void setData(ServerObjectInfo serverObjectInfo) {
        //noinspection DuplicateStringLiteralInspection
        notNull(serverObjectInfo, "Argument serverObjectInfo cannot be a null"); //NON-NLS

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
            // TODO Check for file encoding (What kinds of encoding can be)

            if (getLocation().exists()) {
                return IOUtils.toString(new BufferedInputStream(new FileInputStream(getLocation())));
            } else {
                return "";
            }
        } catch (FileNotFoundException e) {
            //noinspection DuplicateStringLiteralInspection
            logger.error("File not found.", e); //NON-NLS

            showMessageDialog(mainFrame, format(
                    resourceBundle.getString("confEditorInput.loadContent.error.message"),
                    getLocation().getAbsolutePath()),
                    resourceBundle.getString("confEditorInput.loadContent.error.title"),
                    ERROR_MESSAGE);

            return "";
        } catch (IOException e) {
            throw createRuntimeException(e);
        }
    }

    public void saveContent(String content) {
        notNull(content, "Argument content cannot be a null"); //NON-NLS

        try {
            // TODO Hardcoded block. Remove it.
            if (!getLocation().getParentFile().exists()) {
                //noinspection ResultOfMethodCallIgnored
                getLocation().getParentFile().mkdir();
            }

            OutputStream os = new BufferedOutputStream(new FileOutputStream(getLocation()));

            // TODO check about main conf file encoding.
            IOUtils.write(content, os, "UTF-8"); //NON-NLS

            os.close();
        } catch (FileNotFoundException e) {
            //noinspection DuplicateStringLiteralInspection
            logger.error("File not found.", e); //NON-NLS

            // todo localize
            showMessageDialog(mainFrame, format(
                    resourceBundle.getString("confEditorInput.saveContent.error.message"),
                    getLocation().getAbsolutePath()),
                    resourceBundle.getString("confEditorInput.saveContent.error.title"),
                    ERROR_MESSAGE);
        } catch (IOException e) {
            throw createRuntimeException(e);
        }
    }

    public Icon getContentPaneIcon() {
        try {
            return httpServerResourceLocator.loadIcon("config_file_icon.png"); //NON-NLS
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
