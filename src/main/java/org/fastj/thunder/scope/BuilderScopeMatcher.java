package org.fastj.thunder.scope;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;

public class BuilderScopeMatcher implements ScopeMatcher {

    private boolean isBuilderScope(AnActionEvent actionEvent) {
        PsiJavaFile psiJavaFile = (PsiJavaFile) actionEvent.getData(CommonDataKeys.PSI_FILE);
        Caret caret = actionEvent.getData(CommonDataKeys.CARET);
        if (caret == null) {
            return false;
        }
        PsiElement pe = psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
        if (!(pe instanceof PsiIdentifier)) {
            return false;
        }
        PsiIdentifier psiIdentifier = (PsiIdentifier) pe;
        return "builder".equalsIgnoreCase(pe.getText());
    }

    @Override
    public Scope match(AnActionEvent actionEvent) {
        if (isBuilderScope(actionEvent)) {
            return Scope.BUILDER;
        }
        return Scope.UNKNOWN;
    }
}
