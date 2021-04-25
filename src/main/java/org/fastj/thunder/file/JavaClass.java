package org.fastj.thunder.file;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.Optional;

public class JavaClass {

    private PsiClass psiClass;

    public JavaClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public static Optional<JavaClass> fromParameter(PsiParameter parameter) {
        if (!(parameter instanceof PsiClass)) {
            return Optional.empty();
        }
        return Optional.empty();
    }

}
