package org.fastj.thunder.logging;

public class ConsoleViewLogger extends AbstractLogger {

    private final ConsoleViewPrinter consoleViewPrinter;

    public ConsoleViewLogger(String className, ConsoleViewPrinter consoleViewPrinter) {
        super(className);
        this.consoleViewPrinter = consoleViewPrinter;
    }

    @Override
    protected void writeToConsole(String msg) {

    }


    @Override
    public void info(String format, Object... args) {
        consoleViewPrinter.appendInfo(format("INFO ", format, args));
    }

    @Override
    public void debug(String format, Object... args) {
        if (defaultLevel == LogLevel.DEBUG) {
            consoleViewPrinter.appendDebug(format("DEBUG", format, args));
        }
    }

    @Override
    public void error(String format, Object... args) {
        consoleViewPrinter.appendError(format("ERROR", format, args));
    }
}
