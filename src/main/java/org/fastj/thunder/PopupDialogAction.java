package org.fastj.thunder;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.fastj.thunder.logging.LoggerFactory;
import org.fastj.thunder.modifier.CodeModifier;
import org.fastj.thunder.modifier.CodeModifierFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PopupDialogAction extends AnAction {


    @Override
    public void update(@NotNull AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LoggerFactory.setProject(event.getProject());
        Optional<? extends CodeModifier> optional = CodeModifierFactory.getInstance().create(event);
        optional.ifPresent(CodeModifier::tryModify);
    }
}
