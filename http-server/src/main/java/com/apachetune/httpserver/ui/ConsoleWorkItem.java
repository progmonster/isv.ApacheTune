package com.apachetune.httpserver.ui;

import static com.apachetune.core.ui.Constants.*;
import com.apachetune.core.ui.*;
import com.apachetune.core.ui.actions.*;
import com.apachetune.core.ui.editors.*;
import static com.apachetune.httpserver.Constants.*;
import com.apachetune.httpserver.ui.resources.*;
import com.google.inject.*;
import com.google.inject.name.*;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

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

        ToolWindow window = toolWindowManager.getToolWindow(OUTPUT_TOOL_WINDOW);

        window.setVisible(true);

        stdoutPane.addFocusListener(this);

        menuBarManager.createAndBindContextMenu(stdoutPane, this);
    }

    protected void doUIDispose() {
        stdoutPane.removeFocusListener(this);

        stdoutPane = null;

        toolWindowManager.unregisterToolWindow(OUTPUT_TOOL_WINDOW);
    }
}
