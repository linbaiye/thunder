package org.fastj.thunder.completer.mybatis;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.fastj.thunder.completer.AbstractContextAnalyser;
import org.fastj.thunder.context.ThunderEvent;

import java.util.concurrent.atomic.AtomicReference;


public class RepositoryContextAnalyser extends AbstractContextAnalyser {

    private final PsiClass entityClass;

    private final PsiClass repositoryClass;

    private final PsiIdentifier identifier;

    public RepositoryContextAnalyser(ThunderEvent thunderEvent) {
        super(thunderEvent);
        identifier = findIdentifier();
        repositoryClass = findRepositoryClass(identifier);
        entityClass = findEntityClass(repositoryClass);
    }

    private PsiIdentifier findIdentifier() {
        PsiElement psiElement = thunderEvent.getElementAtCaret();
        if (psiElement instanceof PsiIdentifier) {
            return (PsiIdentifier) psiElement;
        }
        AtomicReference<PsiIdentifier> container = new AtomicReference<>();
        psiElement.getParent().acceptChildren(new JavaRecursiveElementVisitor() {
            @Override
            public void visitIdentifier(PsiIdentifier identifier) {
                container.set(identifier);
            }
        });
        return container.get();
    }

    private PsiClass findRepositoryClass(PsiIdentifier psiIdentifier) {
        if (psiIdentifier == null) {
            return null;
        }
        PsiMethodCallExpression callExpression = PsiTreeUtil.getParentOfType(psiIdentifier, PsiMethodCallExpression.class);
        if (callExpression == null) {
            return null;
        }
        PsiReferenceExpression referenceExpression = PsiTreeUtil.findChildOfType(callExpression, PsiReferenceExpression.class);
        if (referenceExpression == null) {
            return null;
        }
        PsiExpression expression = referenceExpression.getQualifierExpression();
        if (expression == null) {
            return null;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(expression.getType());
        if (psiClass == null) {
            return null;
        }
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (annotation.getText() != null && annotation.getText().startsWith("@Repository")) {
                return psiClass;
            }
        }
        return null;
    }

    private PsiClass findEntityClass(PsiClass repositoryClass) {
        if (repositoryClass == null) {
            return null;
        }
        PsiClassType[] psiClassTypes = repositoryClass.getExtendsListTypes();
        if (psiClassTypes.length != 1 || !"BaseMapper".equals(psiClassTypes[0].getName())) {
            return null;
        }
        if (psiClassTypes[0].getParameters().length != 1) {
            return null;
        }
        PsiType psiType = psiClassTypes[0].getParameters()[0];
        if (!(psiType instanceof PsiClassReferenceType)) {
            return null;
        }
        PsiClassReferenceType entityType = (PsiClassReferenceType)psiType;
        return entityType.resolve();
    }

    public PsiClass getEntityClass() {
        return entityClass;
    }

    public PsiClass getRepositoryClass() {
        return repositoryClass;
    }

    public PsiIdentifier getIdentifier() {
        return identifier;
    }
}

