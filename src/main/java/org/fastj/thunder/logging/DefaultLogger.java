package org.fastj.thunder.logging;

public class DefaultLogger extends AbstractLogger {

    public DefaultLogger() {
        super(AbstractLogger.class.getName());
    }

    @Override
    protected void writeToConsole(String msg){
        System.out.print(msg);
    }
}
