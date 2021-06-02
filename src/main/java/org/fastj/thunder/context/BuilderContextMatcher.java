package org.fastj.thunder.context;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

public class BuilderContextMatcher extends AbstractContextMatcher {

    public BuilderContextMatcher(ContextMatcher next) {
        super(next);
    }

    private boolean isBuilderScope(PsiElement element) {
        PsiElement sibling = element.getPrevSibling();
        return sibling != null && sibling.getText().contains(".builder()");
    }

    private boolean isBuilderScope(PsiIdentifier identifier) {
        PsiMethodCallExpression callExpression = PsiTreeUtil.getParentOfType(identifier, PsiMethodCallExpression.class);
        return callExpression != null && callExpression.getText().contains(".builder()");
    }

    private boolean isBuilderScope(ThunderEvent thunderEvent) {
        PsiElement pe = thunderEvent.getElementAtCaret();
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
    protected ContextType doMatch(ThunderEvent thunderEvent) {
        return isBuilderScope(thunderEvent) ? ContextType.BUILDER : null;
    }
}
