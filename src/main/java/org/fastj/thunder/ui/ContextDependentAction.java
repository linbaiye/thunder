package org.fastj.thunder.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class ContextDependentAction extends AnAction {

    protected abstract ContextType getDependentContext();

    protected Set<ContextType> detectContextTypes(AnActionEvent event) {
        Set<ContextType> result = new HashSet<>();
        List<? extends ContextMatcher> matchers = ContextMatcherFactory.getInstance().getMatchers();
        ThunderEvent thunderEvent = new ActionThunderEvent(event);
        for (ContextMatcher matcher : matchers) {
            matcher.addIfMatch(thunderEvent, result);
        }
        return result;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Set<ContextType> contextTypeSet = detectContextTypes(event);
        event.getPresentation().setEnabledAndVisible(contextTypeSet.contains(getDependentContext()));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional<? extends CodeCompleter> optionalCodeCompleter = CodeCompleterFactory.getInstance()
                .create(new ActionThunderEvent(e), getDependentContext());
        optionalCodeCompleter.ifPresent(CodeCompleter::tryComplete);
    }

}
