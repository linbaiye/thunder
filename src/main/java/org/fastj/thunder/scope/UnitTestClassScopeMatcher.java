package org.fastj.thunder.scope;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public class UnitTestClassScopeMatcher implements ScopeMatcher {


    private boolean isUnitTest(AnActionEvent actionEvent) {
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof PsiJavaFile)) {
            return false;
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        return psiJavaFile.getName().endsWith("UT.java") &&
                psiJavaFile.getContainingDirectory() != null &&
                psiJavaFile.getContainingDirectory().getVirtualFile().getPath().contains("src/test/java");
    }


    @Override
    public Scope match(AnActionEvent actionEvent) {
        if (isUnitTest(actionEvent)) {
            return Scope.UNIT_TEST_CLASS;
        }
        return ScopeFinderRegistry.getInstance().getScopeFinder(Scope.REPOSITORY).match(actionEvent);
    }
}
