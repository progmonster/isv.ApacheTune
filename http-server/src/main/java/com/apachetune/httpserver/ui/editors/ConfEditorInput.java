package com.apachetune.httpserver.ui.editors;

import com.apachetune.core.ui.editors.EditorInput;
import com.apachetune.httpserver.entities.ServerObjectInfo;
import com.apachetune.httpserver.ui.resources.HttpServerResourceLocator;
import com.google.inject.Inject;
import jsyntaxpane.jsyntaxkits.HttpdConfSyntaxKit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.io.*;
import java.net.URI;

import static com.apachetune.httpserver.Constants.EDITOR_WORK_ITEM;
import static com.apachetune.httpserver.Constants.TEXT_HTTPDCONF_CONTENT_TYPE;
import static java.io.File.separatorChar;
import static jsyntaxpane.DefaultSyntaxKit.getContentTypes;
import static jsyntaxpane.DefaultSyntaxKit.registerContentType;
import static org.apache.commons.lang.ArrayUtils.contains;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ConfEditorInput implements EditorInput {
    private final HttpServerResourceLocator httpServerResourceLocator;

    private ServerObjectInfo serverObjectInfo;

    @Inject
    public ConfEditorInput(HttpServerResourceLocator httpServerResourceLocator) {
        this.httpServerResourceLocator = httpServerResourceLocator;
    }

    public void setData(ServerObjectInfo serverObjectInfo) {
        if (serverObjectInfo == null) {
            throw new NullPointerException("Argument serverObjectInfo cannot be a null [this = " + this + "]");
        }

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
            // TODO Generate user-frendly message if errors have occured.
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        } catch (IOException e) {
            // TODO Generate user-frendly message if errors have occured.
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    public void saveContent(String content) {
        if (content == null) {
            throw new NullPointerException("Argument content cannot be a null [this = " + this + "]");
        }

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

            // TODO Generate user-frendly message if errors have occured.
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        } catch (IOException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    public Icon getContentPaneIcon() {
        try {
            return httpServerResourceLocator.loadIcon("config_file_icon.png");
        } catch (IOException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
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
