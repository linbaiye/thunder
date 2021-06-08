package org.fastj.thunder.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.fastj.thunder.context.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ContextDependentAction extends AnAction {

    protected Set<ContextType> detectContextTypes(AnActionEvent event) {
        Set<ContextType> result = new HashSet<>();
        List<? extends ContextMatcher> matchers = ContextMatcherFactory.getInstance().getMatchers();
        ThunderEvent thunderEvent = new ActionThunderEvent(event);
        for (ContextMatcher matcher : matchers) {
            matcher.addIfMatch(thunderEvent, result);
        }
        return result;
    }
}
