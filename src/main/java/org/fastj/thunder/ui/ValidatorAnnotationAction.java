package org.fastj.thunder.ui;

import org.fastj.thunder.context.ContextType;

public class ValidatorAnnotationAction extends ContextDependentAction {

    @Override
    protected ContextType getDependentContext() {
        return ContextType.VALIDATOR_ANNOTATIONS;
    }
}
