package com.apachetune.httpserver.ui;

import com.apachetune.core.GenericWorkItem;
import com.apachetune.core.ResourceManager;
import com.apachetune.core.Subscriber;
import com.apachetune.core.ui.OutputPaneDocument;
import com.apachetune.core.ui.actions.ActionHandler;
import com.apachetune.core.ui.actions.ActionManager;
import com.apachetune.core.ui.actions.ActionPermission;
import com.apachetune.httpserver.Constants;
import com.apachetune.httpserver.entities.HttpServer;
import com.apachetune.httpserver.ui.actions.CheckServerActionSite;
import com.apachetune.httpserver.ui.editors.ConfEditorWorkItem;
import com.apachetune.httpserver.ui.impl.HttpServerWorkItemImpl;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtils;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.apachetune.core.ui.Constants.TOOL_WINDOW_MANAGER;
import static com.apachetune.core.utils.Utils.createRuntimeException;
import static com.apachetune.httpserver.Constants.*;
import static java.awt.Color.BLACK;
import static java.awt.Color.RED;
import static java.util.regex.Pattern.*;
import static org.apache.commons.collections.CollectionUtils.find;
import static org.apache.commons.lang.Validate.isTrue;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class CheckMainConfSyntaxWorkItem extends GenericWorkItem implements CheckServerActionSite {
    private static final String ERROR_PATTERN = ".*Syntax error on line (.*) of (.*):\\s.*"; //NON-NLS

    private final OutputPaneDocument outputPaneDocument;

    private final ToolWindowManager toolWindowManager;

    private final ActionManager actionManager;
    
    private HttpServer httpServer;

    private final ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(CheckMainConfSyntaxWorkItem.class);

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
        // TODO May be use temporary conf files instead of saving current ones for checking?
        if (getParentWorkItem().askAndSaveAllConfFiles(
                resourceBundle.getString(
                        "checkMainConfSyntaxWorkItem.onServerCheck.askAndSaveAllConfFilesDialog.title"),
                resourceBundle.getString(
                        "checkMainConfSyntaxWorkItem.onServerCheck.askAndSaveAllConfFilesDialog.message"))) {
            outputPaneDocument.clear();

            checkSyntax();
        }
    }

    @ActionPermission(SERVER_CHECK_CONFIG_SYNTAX_ACTION)
    public boolean isServerCheckEnabled() {
        return true;
    }

    protected void doInitialize() {
        //noinspection DuplicateStringLiteralInspection
        isTrue(hasState(CURRENT_HTTP_SERVER_STATE),
                "An http-server must be opened before create RunServerWorkflowWorkItem [" //NON-NLS
                        + "this = " + this + ']'); //NON-NLS

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

            Process checkProcess = httpServer.executeServerApp("-t"); //NON-NLS

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
            throw createRuntimeException(e);
        } catch (InterruptedException e) {
            throw createRuntimeException(e);
        }
    }

    private HttpServerWorkItemImpl getParentWorkItem() {
        return (HttpServerWorkItemImpl) getParent();
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
