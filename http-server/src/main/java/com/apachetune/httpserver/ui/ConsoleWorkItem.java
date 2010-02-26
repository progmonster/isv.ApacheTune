package com.apachetune.httpserver.ui;

import com.apachetune.core.ui.GenericUIWorkItem;
import com.apachetune.core.ui.MenuBarManager;
import com.apachetune.core.ui.OutputPaneDocument;
import com.apachetune.core.ui.actions.ActionHandler;
import com.apachetune.core.ui.actions.ActionPermission;
import com.apachetune.core.ui.editors.EditorActionSite;
import com.apachetune.httpserver.ui.resources.HttpServerResourceLocator;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

import static com.apachetune.core.ui.Constants.*;
import static com.apachetune.httpserver.Constants.CONSOLE_WORK_ITEM;
import static com.apachetune.httpserver.Constants.OUTPUT_TOOL_WINDOW;
import static org.noos.xing.mydoggy.ToolWindowAnchor.BOTTOM;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
// TODO Move to Core-UI module.
public class ConsoleWorkItem extends GenericUIWorkItem implements EditorActionSite, FocusListener {
    private final ToolWindowManager toolWindowManager;

    private final OutputPaneDocument outputPaneDocument;

    private final HttpServerResourceLocator httpServerResourceLocator;

    private final MenuBarManager menuBarManager;

    private JTextPane stdoutPane;

    @Inject
    public ConsoleWorkItem(OutputPaneDocument outputPaneDocument,
            @Named(TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager,
            HttpServerResourceLocator httpServerResourceLocator, MenuBarManager menuBarManager) {
        super(CONSOLE_WORK_ITEM);

        this.outputPaneDocument = outputPaneDocument;
        this.toolWindowManager = toolWindowManager;
        this.httpServerResourceLocator = httpServerResourceLocator;
        this.menuBarManager = menuBarManager;
    }

    @ActionHandler(EDIT_COPY_ACTION)
    public void onCopy() {
        stdoutPane.copy();
    }

    @ActionPermission(EDIT_COPY_ACTION)
    public boolean isCopyEnabled() {
        return true;
    }

    @ActionHandler(EDIT_CUT_ACTION)
    public void onCut() {
        // No-op
    }

    @ActionPermission(EDIT_CUT_ACTION)
    public boolean isCutEnabled() {
        return false;
    }

    @ActionHandler(EDIT_PASTE_ACTION)
    public void onPaste() {
        // No-op.
    }

    @ActionPermission(EDIT_PASTE_ACTION)
    public boolean isPasteEnabled() {
        return false;
    }

    @ActionHandler(EDIT_SELECT_ALL_ACTION)
    public void onSelectAll() {
        stdoutPane.grabFocus();

        stdoutPane.selectAll();
    }

    @ActionPermission(EDIT_SELECT_ALL_ACTION)
    public boolean isSelectAllEnabled() {
        return true;
    }

    public void focusGained(FocusEvent e) {
        activate();
    }

    public void focusLost(FocusEvent e) {
        // No-op.
    }

    // FIX restore focus for Console if server was closed when this console was active.
    protected void doActivation() {
        getOutputToolWindow().setSelected(true);
        getOutputToolWindow().setActive(true);
    }

    protected void doUIInitialize() {
        stdoutPane = new JTextPane();

        stdoutPane.setDocument(outputPaneDocument);
        stdoutPane.setText("");
        stdoutPane.setEditable(false);

        try {
            // TODO Localize
            toolWindowManager.registerToolWindow(OUTPUT_TOOL_WINDOW, "Output view", httpServerResourceLocator
                    .loadIcon("console_view_icon.png"), stdoutPane, BOTTOM);
        } catch (IOException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }

        ToolWindow window = getOutputToolWindow();

        window.setVisible(true);

        stdoutPane.addFocusListener(this);

        menuBarManager.createAndBindContextMenu(stdoutPane, this);
    }

    protected void doUIDispose() {
        stdoutPane.removeFocusListener(this);

        stdoutPane = null;

        toolWindowManager.unregisterToolWindow(OUTPUT_TOOL_WINDOW);
    }

    private ToolWindow getOutputToolWindow() {
        return toolWindowManager.getToolWindow(OUTPUT_TOOL_WINDOW);
    }
}
