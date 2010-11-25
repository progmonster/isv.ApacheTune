package com.apachetune.httpserver.ui;

import com.apachetune.core.ui.CoreUIUtils;
import com.apachetune.core.ui.GenericUIWorkItem;
import com.apachetune.core.ui.OutputPaneDocument;
import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.apachetune.core.ui.actions.ActionHandler;
import com.apachetune.core.ui.actions.ActionManager;
import com.apachetune.core.ui.actions.ActionPermission;
import com.apachetune.core.utils.StringValue;
import com.apachetune.httpserver.entities.HttpServer;
import com.apachetune.httpserver.ui.actions.RunServerWorkflowActionSite;
import com.apachetune.httpserver.ui.impl.HttpServerWorkItemImpl;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.IOUtils;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static com.apachetune.core.ui.Constants.TOOL_WINDOW_MANAGER;
import static com.apachetune.httpserver.Constants.*;
import static java.awt.Color.BLACK;
import static java.awt.Color.RED;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class RunServerWorkflowWorkItem extends GenericUIWorkItem implements RunServerWorkflowActionSite {
    private final OutputPaneDocument outputPaneDocument;

    private final ToolWindowManager toolWindowManager;

    private final ActionManager actionManager;

    private final StatusBarManager statusBarManager;

    private final CoreUIUtils coreUIUtils;

    private final ExecutorService executorService = newSingleThreadExecutor();

    private HttpServer httpServer;

    private boolean isRelatedActionsDisabled;

    @Inject
    public RunServerWorkflowWorkItem(ActionManager actionManager, OutputPaneDocument outputPaneDocument,
            @Named(TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager, StatusBarManager statusBarManager,
            CoreUIUtils coreUIUtils) {
        super(RUN_SERVER_WORKFLOW_WORK_ITEM);

        this.actionManager = actionManager;
        this.outputPaneDocument = outputPaneDocument;
        this.toolWindowManager = toolWindowManager;
        this.statusBarManager = statusBarManager;
        this.coreUIUtils = coreUIUtils;
    }

    @Override
    public boolean needActionManagerAutobinding() {
        return false;
    }

    @ActionHandler(SERVER_START_HTTP_SERVER_ACTION)
    public void onServerStart() {
        if (getParentWorkItem().askAndSaveAllConfFiles("Save files", "It should to save all changed configuration" +
                    " files before the starting server. \n\nSave this files?")) { // TODO Localize
            executeTasks(createStartServerTask());
        }
    }

    @ActionPermission(SERVER_START_HTTP_SERVER_ACTION)
    public boolean isServerStartEnabled() {
        return !isRelatedActionsDisabled;
    }

    @ActionHandler(SERVER_STOP_HTTP_SERVER_ACTION)
    public void onServerStop() {
        executeTasks(createStopServerTask());
    }

    @ActionPermission(SERVER_STOP_HTTP_SERVER_ACTION)
    public boolean isServerStopEnabled() {
        return !isRelatedActionsDisabled;
    }

    @ActionHandler(SERVER_RESTART_HTTP_SERVER_ACTION)
    public void onServerRestart() {
        if (getParentWorkItem().askAndSaveAllConfFiles("Save files", "It should to save all changed configuration" +
                    " files before the restarting server. \n\nSave this files?")) { // TODO Localize
            executeTasks(createStopServerTask(), createStartServerTask());
        }
    }

    @ActionPermission(SERVER_RESTART_HTTP_SERVER_ACTION)
    public boolean isServerRestartEnabled() {
        return !isRelatedActionsDisabled;
    }

    protected void doUIInitialize() {
        isTrue(hasState(CURRENT_HTTP_SERVER_STATE), "An http-server must be opened before create RunServerWorkflowWorkItem [" +                    "this = " + this + ']');

        httpServer = (HttpServer) getState(CURRENT_HTTP_SERVER_STATE);

        actionManager.activateActionSites(this);
    }

    protected void doUIDispose() {
        actionManager.deactivateActionSites(this);

        httpServer = null;
    }

    private ExecutionTask createStopServerTask() {
        return new ExecutionTask("-k stop", SERVER_STOPPING_STATUS, "Server stopping..."); // TODO Localize.
    }

    private ExecutionTask createStartServerTask() {
        return new ExecutionTask("-k start", SERVER_STARTING_STATUS, "Server starting..."); // TODO Localize.
    }

    private void executeTasks(ExecutionTask... tasks) {
        executorService.submit(new TaskRunner(asList(tasks)));
    }

    private void setStdoutText(final String text, final Color color) {
        Runnable setStdoutTextTask = new Runnable() {
            public void run() {
                outputPaneDocument.setColoredText(text, color);

                ToolWindow outputView = toolWindowManager.getToolWindow(OUTPUT_TOOL_WINDOW);

                if (outputView.isMinimized()) {
                    outputView.setFlashing(true);
                }
            }
        };

        coreUIUtils.safeEDTCall(setStdoutTextTask);
    }

    private HttpServerWorkItemImpl getParentWorkItem() {
        return (HttpServerWorkItemImpl) getParent();
    }

    private class TaskRunner implements Callable<Void> {
        private final List<Callable<Boolean>> tasks;

        public TaskRunner(List<ExecutionTask> tasks) {
            notNull(tasks, "Argument tasks cannot be a null");

            this.tasks = new ArrayList<Callable<Boolean>>(tasks);
        }

        public Void call() throws Exception {
            updateUI(true);

            try {
                for (Callable<Boolean> task : tasks) {
                    task.call();
                }
            } finally {
                tasks.clear();

                updateUI(false);
            }

            return null;
        }

        private void updateUI(boolean isTaskStarted) {
            if (isTaskStarted) {
                setStdoutText("", BLACK);
            }

            isRelatedActionsDisabled = !isTaskStarted;

            actionManager.updateActionSites(this);
        }
    }

    private class ExecutionTask implements Callable<Boolean> {
        private final String command;

        private final String statusBarMessageId;

        private final String statusBarMessage;

        public ExecutionTask(String command, String statusBarMessageId, String statusBarMessage) {
            this.command = command;
            this.statusBarMessageId = statusBarMessageId;
            this.statusBarMessage = statusBarMessage;
        }

        public Boolean call() throws Exception {
            boolean hasError;

            showStatus();

            try {
                StringValue messageValue = new StringValue();

                hasError = doServerControl(command, messageValue);

                setStdoutText(messageValue.value, hasError ? RED : BLACK);
                
                if (hasError) {
                    raiseEvent(ON_HIGHLIGHT_SYNTAX_ERROR_EVENT, messageValue.value, RunServerWorkflowWorkItem.this);
                }
            } finally {
                hideStatus();
            }

            return hasError;
        }

        private void hideStatus() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    statusBarManager.removeMainStatus(statusBarMessageId);
                }
            });
        }

        private void showStatus() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    statusBarManager.addMainStatus(statusBarMessageId, statusBarMessage);
                }
            });
        }

        // TODO write apache's output messages to the output view synchronously
        private boolean doServerControl(String commandLineArguments, StringValue messageValue) {
            String message = "";

            int result = 0;

            boolean hasError = false;

            try {
                Process serverControlProcess = httpServer.executeServerApp(commandLineArguments);

                message = IOUtils.toString(serverControlProcess.getErrorStream());

                result = serverControlProcess.waitFor();
            } catch (Exception e) {
                hasError = true;

                message = join(asList(message, getStackTrace(e)), "\n\n");
            }

            hasError |= (result != 0);

            if (message.isEmpty() && !hasError) {
                messageValue.value = "OK"; // TODO Localize.
            } else {
                messageValue.value = message;
            }

            return hasError;
        }
    }
}
