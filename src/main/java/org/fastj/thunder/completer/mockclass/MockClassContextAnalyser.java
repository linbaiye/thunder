package org.fastj.thunder.completer.mockclass;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.fastj.thunder.completer.AbstractContextAnalyser;
import org.fastj.thunder.context.ThunderEvent;

public class MockClassContextAnalyser extends AbstractContextAnalyser {

    private final PsiClass mockingClass;

    /**
     * The element to be replaced.
     */
    private final PsiElement elementToReplace;

    public MockClassContextAnalyser(ThunderEvent thunderEvent) {
        super(thunderEvent);
        mockingClass = findMockingClass();
        elementToReplace = findToBeReplacedElement();
    }

    private PsiClass findMockingClass() {
        PsiElement element = thunderEvent.getElementAtCaret();
        if (!(element.getPrevSibling() instanceof PsiExpressionStatement) &&
                !(element.getPrevSibling() instanceof PsiReferenceExpression)) {
            return null;
        }
        PsiReferenceExpression referenceExpression = element.getPrevSibling() instanceof PsiReferenceExpression ?
                (PsiReferenceExpression) element.getPrevSibling() :
                PsiTreeUtil.findChildOfType(element.getPrevSibling(), PsiReferenceExpression.class);
        if (referenceExpression == null) {
            return null;
        }
        PsiElement qualifier = referenceExpression.getQualifier();
        if (!(qualifier instanceof PsiReferenceExpression)) {
            return null;
        }
        PsiReferenceExpression psiReferenceExpression = (PsiReferenceExpression) qualifier;
        PsiElement psiElement = psiReferenceExpression.resolve();
        return psiElement instanceof PsiClass ? (PsiClass) psiElement : null;
    }

    private PsiElement findToBeReplacedElement() {
        PsiElement psiElement = thunderEvent.getElementAtCaret();
        PsiElement sibling = psiElement.getPrevSibling();
        return sibling instanceof PsiExpressionStatement ||
                sibling instanceof PsiReferenceExpression ? sibling : null;
    }

    /**
     * Gets the class we will be mocking.
     * @return
     */
    public PsiClass getMockingClass() {
        return mockingClass;
    }

    public PsiElement getElementToReplace() {
        return elementToReplace;
    }
}
