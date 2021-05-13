package org.fastj.thunder.scope;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class DefaultScopeMatcher implements ScopeMatcher {

    @Override
    public Scope match(AnActionEvent actionEvent) {
        return ScopeFinderRegistry.getInstance().getScopeFinder(Scope.UNIT_TEST_CLASS).match(actionEvent);
    }
}
