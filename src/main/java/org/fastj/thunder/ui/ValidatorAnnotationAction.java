package org.fastj.thunder.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.ActionThunderEvent;
import org.fastj.thunder.context.ContextType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public class ValidatorAnnotationAction extends ContextDependentAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        Set<ContextType> contextTypeSet = detectContextTypes(event);
        event.getPresentation().setEnabledAndVisible(contextTypeSet.contains(ContextType.VALIDATOR_ANNOTATIONS));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional<? extends CodeCompleter> optionalCodeCompleter = CodeCompleterFactory.getInstance()
                .create(new ActionThunderEvent(e), ContextType.VALIDATOR_ANNOTATIONS);
        optionalCodeCompleter.ifPresent(CodeCompleter::tryComplete);
    }

}
