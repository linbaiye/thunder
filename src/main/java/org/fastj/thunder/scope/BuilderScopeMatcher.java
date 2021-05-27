package org.fastj.thunder.scope;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.concurrent.atomic.AtomicReference;

public class BuilderScopeMatcher extends AbstractScopeMatcher {

    public BuilderScopeMatcher(ScopeMatcher next) {
        super(next);
    }

    private boolean isBuilderScope(PsiElement element) {
        PsiElement sibling = element.getPrevSibling();
        if (sibling instanceof PsiExpressionStatement ||
                sibling instanceof PsiLocalVariable ||
                sibling instanceof PsiDeclarationStatement ||
                sibling instanceof PsiMethodCallExpression ||
                sibling instanceof PsiReturnStatement) {
            return sibling.getText().endsWith(".builder()");
        }
        AtomicReference<Boolean> reference = new AtomicReference<>(false);
        if (sibling instanceof PsiLambdaExpression) {
            sibling.acceptChildren(new JavaElementVisitor() {
                @Override
                public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                    reference.set(expression.getText().contains(".builder()"));
                }
            });
        }
        return reference.get();
    }

    private boolean isBuilderScope(PsiIdentifier identifier) {
        PsiMethodCallExpression callExpression = PsiTreeUtil.getParentOfType(identifier, PsiMethodCallExpression.class);
        return callExpression != null && callExpression.getText().contains(".builder()");
    }

    private boolean isBuilderScope(ThunderEvent thunderEvent) {
        PsiElement pe = thunderEvent.getElementBeforeCaret();
        if (pe == null) {
            return false;
        }
        if (pe instanceof PsiIdentifier) {
            return isBuilderScope((PsiIdentifier)pe);
        } else {
            return isBuilderScope(pe);
        }
    }

    @Override
    protected ScopeType doMatch(ThunderEvent thunderEvent) {
        return isBuilderScope(thunderEvent) ? ScopeType.BUILDER : null;
    }
}
