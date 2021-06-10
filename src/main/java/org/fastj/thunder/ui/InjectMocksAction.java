package org.fastj.thunder.ui;

import org.fastj.thunder.context.ContextType;

public class InjectMocksAction extends ContextDependentAction {

    @Override
    protected ContextType getDependentContext() {
        return ContextType.INJECT_MOCKS;
    }
}
