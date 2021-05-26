package org.fastj.thunder.modifier.builder;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import org.fastj.thunder.until.NamingUtil;

import java.util.Map;

public class SimpleParameterSelector implements ParameterSelector {

    private final Map<String, PsiType> parameterCandidates;

    public SimpleParameterSelector(Map<String, PsiType> parameterCandidates) {
        this.parameterCandidates = parameterCandidates;
    }

    @Override
    public String selectParameterExpression(String builderMethodName) {
        if (parameterCandidates.containsKey(builderMethodName)) {
            return builderMethodName;
        }
        for (Map.Entry<String, PsiType> entry: parameterCandidates.entrySet()) {
            PsiClass psiClass = PsiTypesUtil.getPsiClass(entry.getValue());
            if (psiClass == null) {
                continue;
            }
            PsiField field = psiClass.findFieldByName(builderMethodName, true);
            if (field != null) {
                return entry.getKey() + ".get" + NamingUtil.capitalFirstChar(field.getName()) + "()";
            }
        }
        return null;
    }

    private boolean isJreType(PsiType type) {
        return type.getCanonicalText().startsWith("java") ||
                type instanceof PsiPrimitiveType;
    }

}
