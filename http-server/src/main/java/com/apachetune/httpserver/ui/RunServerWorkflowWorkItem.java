package com.apachetune.httpserver.ui;

import static com.apachetune.core.ui.Constants.*;
import com.apachetune.core.ui.*;
import com.apachetune.core.ui.actions.*;
import com.apachetune.core.utils.StringValue;
import static com.apachetune.httpserver.Constants.*;
import com.apachetune.httpserver.entities.*;
import com.apachetune.httpserver.ui.actions.*;
import com.google.inject.*;
import com.google.inject.name.*;
import org.apache.commons.io.*;
import static org.apache.commons.lang.StringUtils.*;
import static org.apache.commons.lang.exception.ExceptionUtils.*;
import org.noos.xing.mydoggy.*;

import javax.swing.*;
import java.awt.*;
import static java.awt.Color.*;
import java.util.ArrayList;
import static java.util.Arrays.*;
import java.util.List;
import java.util.concurrent.*;
import static java.util.concurrent.Executors.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
        executeTasks(createStartServerTask());
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
        executeTasks(createStopServerTask(), createStartServerTask());
    }

    @ActionPermission(SERVER_RESTART_HTTP_SERVER_ACTION)
    public boolean isServerRestartEnabled() {
        return !isRelatedActionsDisabled;
    }

    protected void doUIInitialize() {
        if (!hasState(CURRENT_HTTP_SERVER_STATE)) {
            throw new IllegalStateException("An http-server must be opened before create RunServerWorkflowWorkItem [" +
                    "this = " + this + ']');
        }

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

    private class TaskRunner implements Callable<Void> {
        private final List<Callable<Boolean>> tasks;

        public TaskRunner(List<ExecutionTask> tasks) {
            if (tasks == null) {
                throw new NullPointerException("Argument tasks cannot be a null [this = " + this + "]");
            }
            
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
