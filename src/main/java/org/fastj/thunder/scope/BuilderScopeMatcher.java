package org.fastj.thunder.scope;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

public class BuilderScopeMatcher implements ScopeMatcher {


    private boolean isBuilderScope(PsiWhiteSpace psiWhiteSpace) {
        PsiElement sibling = psiWhiteSpace.getPrevSibling();
        if (sibling instanceof PsiExpressionStatement ||
            sibling instanceof PsiLocalVariable ||
                sibling instanceof PsiDeclarationStatement) {
            return sibling.getText().endsWith("builder()");
        }
        return false;
    }

    private boolean isBuilderScope(PsiIdentifier identifier) {
        PsiMethodCallExpression callExpression = PsiTreeUtil.getParentOfType(identifier, PsiMethodCallExpression.class);
        return callExpression != null && callExpression.getText().contains("builder()");
    }

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
        if (pe instanceof PsiWhiteSpace) {
            return isBuilderScope((PsiWhiteSpace)pe);
        } else if (pe instanceof PsiIdentifier) {
            return isBuilderScope((PsiIdentifier)pe);
        }
        return false;
    }

    @Override
    public Scope match(AnActionEvent actionEvent) {
        if (isBuilderScope(actionEvent)) {
            return Scope.BUILDER;
        }
        return Scope.UNKNOWN;
    }

}
