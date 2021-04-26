package org.fastj.thunder.logging;


import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.fastj.thunder.toolwindow.Icons;

public class LoggingManager {


    private ConsoleView consoleView;

    private final static Key<LoggingManager> CONSOLE_VEW_KEY = new Key<>("CONSOLE_VEW_KEY");

    private LoggingManager(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }

    public void activateLoggingConsole(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Thunder");
        if (toolWindow == null) {
            return;
        }
        Content content = toolWindow.getContentManager().getContent(0);
        if (content == null) {
            return;
        }
        consoleView = (ConsoleView) content.getComponent();
        toolWindow.activate(()->{}, true, true);
        toolWindow.getContentManager().setSelectedContent(content);
    }

    public void appendError(String msg) {
        if (consoleView != null) {
            consoleView.print(msg, ConsoleViewContentType.ERROR_OUTPUT);
        }
    }

    public void appendDebug(String msg) {
        if (consoleView != null) {
            consoleView.print(msg, ConsoleViewContentType.NORMAL_OUTPUT);
        }
    }

    public void appendInfo(String msg) {
        if (consoleView != null) {
            consoleView.print(msg, ConsoleViewContentType.NORMAL_OUTPUT);
        }
    }

    public static LoggingManager getInstance(Project project) {
        LoggingManager loggingManager = project.getUserData(CONSOLE_VEW_KEY);
        if (loggingManager == null) {
            ToolWindow outputWindow = ToolWindowManager.getInstance(project).registerToolWindow("Thunder",
                    true, ToolWindowAnchor.BOTTOM);
            if (Icons.THUNDER_ICON != null) {
                outputWindow.setIcon(Icons.THUNDER_ICON);
            }
            ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
            Content content = outputWindow.getContentManager().getFactory().createContent(consoleView.getComponent(),
                    "Output", false);
            outputWindow.getContentManager().addContent(content);
            loggingManager = new LoggingManager(consoleView);
            loggingManager.activateLoggingConsole(project);
            project.putUserData(CONSOLE_VEW_KEY, loggingManager);
        }
        return loggingManager;
    }

}
