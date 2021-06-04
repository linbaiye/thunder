package org.fastj.thunder.context;

/**
 * The context inside which the plug-in was triggered.
 */
public enum ContextType {

    UNIT_TEST_CLASS,

    MYBATIS_METHOD_PARAMETER,

    /**
     * We are calling some builder method.
     */
    BUILDER,

    /**
     * We are trying to mock a class.
     */
    MOCK_CLASS,

    UNKNOWN,
    ;



}
