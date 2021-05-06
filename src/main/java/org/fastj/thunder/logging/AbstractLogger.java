package org.fastj.thunder.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractLogger implements Logger {

    private String className;

    public enum LogLevel {
        DEBUG,
        INFO,
        ERROR
    }

    public static LogLevel defaultLevel = LogLevel.INFO;


    public AbstractLogger(String className) {
        this.className = className;
    }

    abstract protected void writeToConsole(String msg);

    protected String formatArgs(String format, Object ... args) {
        if (format == null) {
            return "";
        }
        if (args == null || args.length == 0) {
            return format;
        }
        String[] strings = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof String) {
                strings[i] = (String)arg;
            } else if (arg instanceof Exception) {
                Exception exception = (Exception) arg;
                String tmp = exception.getMessage();
                try (StringWriter sw = new StringWriter();
                     PrintWriter pw = new PrintWriter(sw);) {
                    exception.printStackTrace(pw);
                    tmp = sw.toString();
                } catch (Exception e) {
                }
                strings[i] = tmp;
            } else if (arg != null ){
                strings[i] = arg.toString();
            } else {
                strings[i] = "null";
            }
        }
        for (int i = 0; i < args.length; i++) {
            if (format.contains("{}")) {
                format = format.replaceFirst("\\{\\}", "%s");
            } else {
                break;
            }
        }
        return String.format(format, (Object[])strings);
    }

    protected String format(String level, String format, Object ... args) {
        String tmp = formatArgs(format, args);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String timestamp = simpleDateFormat.format(new Date());
        return  timestamp + " " + level + " " + className + " - " + tmp + "\n";
    }

    private void formatAndWrite(String level, String format, Object ... args) {
        writeToConsole(format(level, format, args));
    }

    @Override
    public void info(String format, Object... args) {
        formatAndWrite("INFO ", format, args);
    }

    @Override
    public void error(String format, Object... args) {
        formatAndWrite("ERROR", format, args);
    }
    @Override
    public void debug(String format, Object... args) {
        if (defaultLevel == LogLevel.DEBUG) {
            formatAndWrite("DEBUG", format, args);
        }
    }
}