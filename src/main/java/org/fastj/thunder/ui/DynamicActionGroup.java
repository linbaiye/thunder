package org.fastj.thunder.ui;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicActionGroup extends ActionGroup {

    @Override
    public @NotNull AnAction[] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[] {new ValidatorAnnotationAction()};
    }
}
