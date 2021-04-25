package org.fastj.thunder.method;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.Optional;

public class JavaMethod {

    private PsiMethod psiMethod;

    public PsiParameter[] getParameters() {
        return psiMethod.getParameterList().getParameters();
    }

    public JavaMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public static Optional<JavaMethod> fromContainingMethodOrSelf(PsiElement element) {
        PsiElement iter = element;
        do {
            if (iter instanceof PsiMethod) {
                return Optional.of(new JavaMethod((PsiMethod) iter));
            }
            iter = element.getParent();
        } while (iter != null);
        return Optional.empty();
    }
}
