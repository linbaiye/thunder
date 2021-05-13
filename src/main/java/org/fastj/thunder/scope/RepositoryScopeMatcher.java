package org.fastj.thunder.scope;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public class RepositoryScopeMatcher implements ScopeMatcher {

    private boolean isInsideRepositoryScope(AnActionEvent actionEvent) {
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof PsiJavaFile)) {
            return false;
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        return (psiJavaFile.getName().endsWith("Repository.java") ||
                psiJavaFile.getName().endsWith("RepositoryImpl.java")) &&
                psiJavaFile.getClasses().length == 1 &&
                psiJavaFile.getContainingDirectory().getVirtualFile().getPath().contains("src/main/java") &&
                psiJavaFile.getPackageName().endsWith(".repository");
    }

    @Override
    public Scope match(AnActionEvent actionEvent) {
        if (isInsideRepositoryScope(actionEvent)) {
            return Scope.REPOSITORY;
        }
        return ScopeFinderRegistry.getInstance().getScopeFinder(Scope.BUILDER).match(actionEvent);
    }
}
