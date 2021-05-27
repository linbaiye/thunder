package org.fastj.thunder.scope;

/**
 * The scope inside which the plug-in was triggered.
 */
public enum ScopeType {

    UNIT_TEST_CLASS,

    REPOSITORY,

    /**
     * We are calling some builder method.
     */
    BUILDER,

    UNKNOWN,
    ;



}
