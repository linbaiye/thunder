package org.fastj.thunder.logging;

public interface Logger {

    void info(String format, Object ... args);

    void error(String format, Object ... args);

    void debug(String format, Object ... args);
}

