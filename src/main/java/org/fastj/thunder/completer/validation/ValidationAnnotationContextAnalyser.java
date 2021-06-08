package org.fastj.thunder.completer.validation;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.fastj.thunder.completer.AbstractContextAnalyser;
import org.fastj.thunder.context.ThunderEvent;

public class ValidationAnnotationContextAnalyser extends AbstractContextAnalyser  {

    private final PsiField[] javaFields;

    public ValidationAnnotationContextAnalyser(ThunderEvent thunderEvent) {
        super(thunderEvent);
        this.javaFields = findAllFields();
    }

    private PsiField[] findAllFields() {
        PsiElement element = thunderEvent.getElementAtCaret();
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (psiClass != null) {
            PsiModifierList psiModifierList = psiClass.getModifierList();
            if (psiModifierList != null) {
                return psiClass.getAllFields();
            }
        }
        return new PsiField[0];
    }

    public PsiField[] getJavaFields() {
        return javaFields;
    }
}
