package org.fastj.thunder.logging;

import com.intellij.openapi.project.Project;

public class LoggerFactory {

    private static Class<? extends Logger> loggerClass = ConsoleViewLogger.class;

    private static final Logger DEFAULT_LOGGER = new DefaultLogger();

    private static Project project;

    public static void setProject(Project project) {
        synchronized (LoggerFactory.class) {
            if (project != null) {
                LoggerFactory.project = project;
            }
        }
    }

    public static Logger getLogger(Class<?> clazz) {
        if (project != null) {
            return new ConsoleViewLogger(clazz.getSimpleName(), ConsoleViewPrinter.getInstance(project));
        } else {
            return DEFAULT_LOGGER;
        }
    }

}
