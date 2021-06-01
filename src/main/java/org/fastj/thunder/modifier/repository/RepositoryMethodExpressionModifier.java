package org.fastj.thunder.modifier.repository;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import org.fastj.thunder.modifier.CodeModifier;

public class RepositoryMethodExpressionModifier implements CodeModifier  {
    private final RepositoryContextParser contextParser;

    private final PsiIdentifier psiIdentifier;

    private final PsiClass psiClass;

    public RepositoryMethodExpressionModifier(RepositoryContextParser contextParser) {
        this.contextParser = contextParser;
        psiIdentifier = findIdentifier();
    }

    private PsiIdentifier findIdentifier() {
        if (contextParser.getElementAtCaret() instanceof PsiIdentifier) {
            return (PsiIdentifier) this.contextParser.getElementAtCaret();
        }
        return null;
    }

    private PsiClass findEntityClass() {

    }

    @Override
    public void tryModify() {

    }
}
