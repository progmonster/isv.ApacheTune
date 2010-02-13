package com.apachetune.httpserver.ui;

import com.apachetune.core.*;
import static com.apachetune.core.ui.Constants.*;
import com.apachetune.core.ui.*;
import com.apachetune.core.ui.actions.*;
import com.apachetune.httpserver.Constants;
import static com.apachetune.httpserver.Constants.*;
import com.apachetune.httpserver.entities.*;
import com.apachetune.httpserver.ui.actions.*;
import com.apachetune.httpserver.ui.editors.*;
import com.google.inject.*;
import com.google.inject.name.*;
import static org.apache.commons.collections.CollectionUtils.*;
import org.apache.commons.collections.*;
import org.apache.commons.io.*;
import org.noos.xing.mydoggy.*;

import static java.awt.Color.*;
import java.io.*;
import java.util.regex.*;
import static java.util.regex.Pattern.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
