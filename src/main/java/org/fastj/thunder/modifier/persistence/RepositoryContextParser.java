package org.fastj.thunder.modifier.persistence;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import org.fastj.thunder.scope.ThunderEvent;


public class RepositoryContextParser {

    private final ThunderEvent thunderEvent;

    private final PsiClass psiClass;

    public RepositoryContextParser(ThunderEvent thunderEvent) {
        this.thunderEvent = thunderEvent;
        PsiJavaFile psiJavaFile = (PsiJavaFile)thunderEvent.getFile();
        psiClass = psiJavaFile.getClasses()[0];
    }

    /**
     * The method which contains caret.
     * @return
     */
    public PsiMethod findCurrentMethod() {
        PsiElement pe = thunderEvent.getElementBeforeCaret();
        return findMethod(pe);
    }

    private PsiElement getFocusedElement() {
        return thunderEvent.getElementBeforeCaret();
    }

    private PsiMethod findMethod(PsiElement element) {
        PsiMethod method = (element instanceof PsiMethod) ? (PsiMethod) element :
                PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method != null && method.getContainingClass() instanceof PsiAnonymousClass) {
            return findMethod(method.getParent());
        }
        return method;
    }

    public PsiIdentifier findDaoIdentifierNearFocusedElement() {
        PsiElement psiElement = getFocusedElement();
        if (psiElement instanceof PsiIdentifier || psiElement == null) {
            return (PsiIdentifier) psiElement;
        }
        PsiElement element = psiElement.getPrevSibling();
        if (!(element instanceof PsiExpressionStatement)) {
            return null;
        }
        PsiExpressionStatement expressionStatement = (PsiExpressionStatement) psiElement.getPrevSibling();
        return PsiTreeUtil.findChildOfType(expressionStatement, PsiIdentifier.class);
    }

    public PsiField findDaoField() {
        PsiIdentifier identifier = findDaoIdentifierNearFocusedElement();
        if (identifier == null) {
            return null;
        }
        return psiClass.findFieldByName(identifier.getText(), false);
    }

    public PsiClass findEntityClass() {

        PsiField psiField = findDaoField();

        if (psiField == null || !(psiField.getType() instanceof PsiClassReferenceType)) {
            return null;
        }
        PsiClassReferenceType type = (PsiClassReferenceType)psiField.getType();
        PsiClass psiClass = type.resolve();
        if (psiClass == null || !psiClass.isInterface()) {
            return null;
        }
        PsiClassType[] psiClassTypes = psiClass.getExtendsListTypes();
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

}

