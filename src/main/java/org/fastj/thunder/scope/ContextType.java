package org.fastj.thunder.scope;

/**
 * The context inside which the plug-in was triggered.
 */
public enum ContextType {

    UNIT_TEST_CLASS,

    REPOSITORY_METHOD,

    /**
     * We are calling some builder method.
     */
    BUILDER,

    UNKNOWN,
    ;



}
