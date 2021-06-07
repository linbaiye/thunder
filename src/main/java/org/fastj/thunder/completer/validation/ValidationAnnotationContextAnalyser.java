package org.fastj.thunder.completer.validation;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.*;
import org.fastj.thunder.completer.AbstractContextAnalyser;
import org.fastj.thunder.context.ThunderEvent;

public class ValidationAnnotationContextAnalyser extends AbstractContextAnalyser  {

    private final PsiField[] javaFields;

    public ValidationAnnotationContextAnalyser(ThunderEvent thunderEvent) {
        super(thunderEvent);
        this.javaFields = findAllFields();
    }

    private PsiField[] findAllFields() {
        PsiJavaFile psiJavaFile = (PsiJavaFile) thunderEvent.getFile();
        PsiClass[] classes = psiJavaFile.getClasses();
        for (PsiClass aClass : classes) {
            PsiModifierList psiModifierList = aClass.getModifierList();
            if (psiModifierList != null &&
                    psiModifierList.hasExplicitModifier(PsiModifier.PUBLIC)) {
                return aClass.getAllFields();
            }
        }
        return new PsiField[0];
    }

    public PsiField[] getJavaFields() {
        return javaFields;
    }
}
