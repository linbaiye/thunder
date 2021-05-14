package org.fastj.thunder.scope;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.psi.*;

public class BuilderScopeMatcher implements ScopeMatcher {

    private boolean isBuilderScope(AnActionEvent actionEvent) {
        PsiJavaFile psiJavaFile = (PsiJavaFile) actionEvent.getData(CommonDataKeys.PSI_FILE);
        Caret caret = actionEvent.getData(CommonDataKeys.CARET);
        if (caret == null || psiJavaFile == null) {
            return false;
        }
        PsiElement pe = psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
        if (pe == null) {
            return false;
        }
        PsiElement psiElement = pe.getParent();
        if (!(psiElement instanceof PsiReferenceExpression)) {
            return false;
        }
        PsiReferenceExpression referenceExpression = (PsiReferenceExpression) psiElement;
        return referenceExpression.getText().contains("builder()");
    }

    @Override
    public Scope match(AnActionEvent actionEvent) {
        if (isBuilderScope(actionEvent)) {
            return Scope.BUILDER;
        }
        return Scope.UNKNOWN;
    }

}
