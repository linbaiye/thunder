package org.example.thunder;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.example.thunder.method.JavaMethod;
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
        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        Optional<JavaMethod> optionalMethod = JavaMethod.fromContainingMethodOrSelf(psiElement);
        optionalMethod.ifPresent(e -> {
            StringBuilder stringBuilder = new StringBuilder();
            Messages.showInfoMessage(stringBuilder.toString(), "Arguments");
        });
    }
}
