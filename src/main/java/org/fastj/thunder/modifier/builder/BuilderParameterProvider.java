package org.fastj.thunder.modifier.builder;

/**
 * Given a builder method name, a BuilderParameterProvider tries to
 * supply it with an expression string from the context,
 * be it a local variable, a method calling expression.
 */
public interface BuilderParameterProvider {

    /**
     * Provides the parameter expression if any.
     * @param builderMethodName
     * @return the builder method parameter expression, or null.
     */
    String provideParameterExpression(String builderMethodName);

}
