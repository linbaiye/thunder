package org.fastj.thunder.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.fastj.thunder.context.ContextType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class InjectMocksAction extends ContextDependentAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Set<ContextType> contextTypes = detectContextTypes(e);
        e.getPresentation().setEnabledAndVisible(contextTypes.contains(ContextType.INJECT_MOCKS));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {


    }
}
