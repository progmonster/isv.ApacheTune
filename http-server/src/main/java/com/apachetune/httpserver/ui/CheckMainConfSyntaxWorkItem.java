package com.apachetune.httpserver.ui;

import com.apachetune.core.GenericWorkItem;
import com.apachetune.core.Subscriber;
import com.apachetune.core.ui.OutputPaneDocument;
import com.apachetune.core.ui.actions.ActionHandler;
import com.apachetune.core.ui.actions.ActionManager;
import com.apachetune.core.ui.actions.ActionPermission;
import com.apachetune.httpserver.Constants;
import com.apachetune.httpserver.entities.HttpServer;
import com.apachetune.httpserver.ui.actions.CheckServerActionSite;
import com.apachetune.httpserver.ui.editors.ConfEditorWorkItem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtils;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.apachetune.core.ui.Constants.TOOL_WINDOW_MANAGER;
import static com.apachetune.httpserver.Constants.*;
import static java.awt.Color.BLACK;
import static java.awt.Color.RED;
import static java.util.regex.Pattern.*;
import static org.apache.commons.collections.CollectionUtils.find;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class CheckMainConfSyntaxWorkItem extends GenericWorkItem implements CheckServerActionSite {
    private static final String ERROR_PATTERN = ".*Syntax error on line (.*) of (.*):\\s.*";

    private final OutputPaneDocument outputPaneDocument;

    private final ToolWindowManager toolWindowManager;

    private final ActionManager actionManager;
    
    private HttpServer httpServer;

    @Inject
    public CheckMainConfSyntaxWorkItem(OutputPaneDocument outputPaneDocument,
            @Named(TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager, ActionManager actionManager) {
        super(Constants.CHECK_SYNTAX_WORK_ITEM);

        this.outputPaneDocument = outputPaneDocument;
        this.toolWindowManager = toolWindowManager;
        this.actionManager = actionManager;
    }

    @ActionHandler(SERVER_CHECK_CONFIG_SYNTAX_ACTION)
    public void onServerCheck() {
        // TODO May be uses temporary conf files instead of saving current ones for checking?
        if (getParentWorkItem().askAndSaveAllConfFiles("Save files", "It should to save all changed configuration" +
                " files before the syntax checking. \n\nSave this files?")) { // TODO Localize
            outputPaneDocument.clear();

            checkSyntax();
        }
    }

    @ActionPermission(SERVER_CHECK_CONFIG_SYNTAX_ACTION)
    public boolean isServerCheckEnabled() {
        return true;
    }

    protected void doInitialize() {
        if (!hasState(CURRENT_HTTP_SERVER_STATE)) {
            throw new IllegalStateException("An http-server must be opened before create RunServerWorkflowWorkItem [" +
                    "this = " + this + ']');
        }

        httpServer = (HttpServer) getState(CURRENT_HTTP_SERVER_STATE);

        actionManager.activateActionSites(this);
    }

    protected void doDispose() {
        httpServer = null;

        actionManager.deactivateActionSites(this);
    }

    private void checkSyntax() {
        try {
            outputPaneDocument.setText("");

            Process checkProcess = httpServer.executeServerApp("-t");

            int result = checkProcess.waitFor();

            boolean hasError = (result != 0);

            String message = IOUtils.toString(checkProcess.getErrorStream());

            outputPaneDocument.setColoredText(message, hasError ? RED : BLACK);

            ToolWindow outputView = toolWindowManager.getToolWindow(Constants.OUTPUT_TOOL_WINDOW);

            if (outputView.isMinimized()) {
                outputView.setFlashing(true);
            }

            if (hasError) {
                highlightError(message);
            }
        } catch (IOException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        } catch (InterruptedException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    private HttpServerWorkItem getParentWorkItem() {
        return (HttpServerWorkItem) getParent();
    }

    @Subscriber(eventId = ON_HIGHLIGHT_SYNTAX_ERROR_EVENT)
    public void highlightError(String errorMessage) {
        Pattern errorPattern = compile(ERROR_PATTERN, MULTILINE | CASE_INSENSITIVE |
                UNICODE_CASE);

        Matcher matcher = errorPattern.matcher(errorMessage);

        if (matcher.find()) {
            int lineNumber = Integer.parseInt(matcher.group(1));

            String fileName = matcher.group(2);

            highlightLineAndFocus(fileName, lineNumber);
        }
    }

    private void highlightLineAndFocus(String fileName, int lineNumber) {
        final File confFile = new File(fileName);
        
        ConfEditorWorkItem confEditorWorkItem = (ConfEditorWorkItem) find(getParentWorkItem().getConfEditorWorkItems(),
                new Predicate() {
            public boolean evaluate(Object object) {
                ConfEditorWorkItem confEditorWorkItem = (ConfEditorWorkItem) object;

                return confEditorWorkItem.getData().getLocation().equals(confFile); 
            }
        });

        confEditorWorkItem.activate();

        int errorLineStartPos = confEditorWorkItem.getLineStartPosition(lineNumber); 

        confEditorWorkItem.setCaretPosition(errorLineStartPos);

        confEditorWorkItem.highlightLine(lineNumber, RED);
    }
}
