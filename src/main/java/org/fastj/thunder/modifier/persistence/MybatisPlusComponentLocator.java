package org.fastj.thunder.modifier.persistence;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;

import java.util.Optional;

public class MybatisPlusComponentLocator implements PersistentComponentLocator {

    @Override
    public Optional<PsiClass> locateEntity(PsiField maybeDaoOrRepository) {
        if (!(maybeDaoOrRepository.getType() instanceof PsiClassReferenceType)) {
            return Optional.empty();
        }
        PsiClassReferenceType type = (PsiClassReferenceType)maybeDaoOrRepository.getType();
        PsiClass psiClass = type.resolve();
        return psiClass != null ? locateEntity(psiClass) : Optional.empty();
    }



    @Override
    public Optional<PsiClass> locateEntity(PsiClass maybeDaoOrRepository) {
        if (!maybeDaoOrRepository.isInterface()) {
            return Optional.empty();
        }
        PsiClassType[] psiClassTypes = maybeDaoOrRepository.getExtendsListTypes();
        if (psiClassTypes.length != 1 && !"BaseMapper".equals(psiClassTypes[0].getName())) {
            return Optional.empty();
        }
        if (psiClassTypes[0].getParameters().length == 0) {
            return Optional.empty();
        }
        PsiType psiType = psiClassTypes[0].getParameters()[0];
        if (!(psiType instanceof PsiClassReferenceType)) {
            return Optional.empty();
        }
        PsiClassReferenceType type = (PsiClassReferenceType)psiType;
        PsiClass psiClass = type.resolve();
        return psiClass != null ? Optional.of(psiClass) : Optional.empty();
    }

}
