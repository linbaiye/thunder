package org.fastj.thunder;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.fastj.thunder.logging.LoggingManager;
import org.fastj.thunder.modifier.UnitTestClassModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PopupDialogAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    private void print(Project project, String message) {
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        if (psiFile instanceof PsiJavaFile) {
            try {
                Optional<UnitTestClassModifier> optional = UnitTestClassModifier.create((PsiJavaFile) psiFile);
                optional.ifPresent(UnitTestClassModifier::tryModify);
                LoggingManager.getInstance(psiFile.getProject()).appendInfo("Mocked fields.");
            } catch (Exception e) {
                LoggingManager.getInstance(psiFile.getProject()).appendError("Mocked fields.");
            }
        }
    }
}
