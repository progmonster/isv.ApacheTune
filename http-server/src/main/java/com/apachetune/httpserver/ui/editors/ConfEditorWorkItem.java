package com.apachetune.httpserver.ui.editors;

import com.apachetune.core.ui.GenericUIWorkItem;
import com.apachetune.core.ui.TitleBarManager;
import com.apachetune.core.ui.editors.EditorManager;
import com.apachetune.core.ui.editors.EditorWorkItem;
import com.apachetune.core.utils.Utils;
import com.apachetune.httpserver.entities.ServerObjectInfo;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.awt.*;

import static com.apachetune.core.ui.Constants.CONF_EDITOR_WORK_ITEM;
import static com.apachetune.core.ui.TitleBarManager.LEVEL_3;
import static org.apache.commons.lang.StringUtils.removeStart;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ConfEditorWorkItem extends GenericUIWorkItem {
    private static final int TITLE_BAR_FILE_NAME_MAX_LENGTH = 100;

    private final EditorManager editorManager;

    private final TitleBarManager titleBarManager;

    private final Provider<ConfEditorInput> confEditorInputProvider;

    private ServerObjectInfo serverObjectInfo;

    private EditorWorkItem editorWorkItem;

    @Inject
    public ConfEditorWorkItem(EditorManager editorManager, Provider<ConfEditorInput> confEditorInputProvider,
                              TitleBarManager titleBarManager) {
        this.editorManager = editorManager;
        this.confEditorInputProvider = confEditorInputProvider;
        this.titleBarManager = titleBarManager;
    }

    public void setData(ServerObjectInfo serverObjectInfo) {
        if (serverObjectInfo == null) {
            throw new NullPointerException("Argument serverObjectInfo cannot be a null [this = " + this + "]");
        }

        this.serverObjectInfo = serverObjectInfo;

        setId(CONF_EDITOR_WORK_ITEM + "::" + serverObjectInfo.getLocation().getAbsolutePath());
    }

    public ServerObjectInfo getData() {
        return serverObjectInfo;
    }

    public void save() {
        editorWorkItem.save();
    }

    public boolean isDirty() {
        return editorWorkItem.isDirty();
    }

    public int getLineStartPosition(int lineNum) {
        return editorWorkItem.getLineStartPosition(lineNum);
    }

    public void setCaretPosition(int position) {
        editorWorkItem.setCaretPosition(position);
    }

    public void highlightLine(int lineNumber, Color red) {
        editorWorkItem.highlightLine(lineNumber, red);
    }

    protected void doUIInitialize() {
        ConfEditorInput confEditorInput = confEditorInputProvider.get();

        confEditorInput.setData(serverObjectInfo);

        editorWorkItem = editorManager.createEditorWorkItem(confEditorInput);

        editorWorkItem.setEditorInput(confEditorInput);

        addChildWorkItem(editorWorkItem);

        editorWorkItem.initialize();
    }

    protected void doUIDispose() {
        editorWorkItem.dispose();

        editorWorkItem = null;
    }

    @Override
    protected void doActivation() {
        editorWorkItem.activate();

        String serverLocation = serverObjectInfo.getHttpServer().getServerRoot().getAbsolutePath();

        String confFileLocation = serverObjectInfo.getLocation().getAbsolutePath();

        if (confFileLocation.startsWith(serverLocation)) {
            confFileLocation = removeStart(confFileLocation, serverLocation);
        }

        titleBarManager.setTitle(LEVEL_3, '[' + Utils.abbreviateFilePath(confFileLocation,
                TITLE_BAR_FILE_NAME_MAX_LENGTH) + ']');

    }

    @Override
    protected void doDeactivation() {
        titleBarManager.removeTitle(LEVEL_3);
    }
}
