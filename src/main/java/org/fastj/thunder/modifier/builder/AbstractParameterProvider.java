package org.fastj.thunder.modifier.builder;

import com.intellij.psi.PsiType;

import java.util.Map;

public abstract class AbstractParameterProvider implements BuilderParameterProvider {

    protected final BuilderParameterProvider next;

    protected final Map<String, PsiType> candidates;

    public AbstractParameterProvider(BuilderParameterProvider next,
                                     Map<String, PsiType> candidates) {
        this.next = next;
        this.candidates = candidates;
    }

    protected abstract String doSelect(String builderMethodName) ;

    @Override
    public String provideParameterExpression(String builderMethodName) {
        String ret = doSelect(builderMethodName);
        if (ret != null) {
            return ret;
        }
        return next != null ? next.provideParameterExpression(builderMethodName) : null;
    }
}
