package org.fastj.thunder.modifier.builder;

/**
 * Given a builder method name, a BuilderParameterProvider tries to
 * provide with an expression string according to the context,
 * be it a local variable, a method calling expression.
 */
public interface BuilderParameterProvider {

    /**
     * Provides the parameter expression if any.
     * @param builderMethodName
     * @return the builder method's parameter expression, or null if no reasonable ones found.
     */
    String provideParameterExpression(String builderMethodName);

}
