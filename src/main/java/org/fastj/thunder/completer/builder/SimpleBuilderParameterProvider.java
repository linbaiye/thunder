package org.fastj.thunder.completer.builder;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import org.fastj.thunder.until.NamingUtil;
import java.util.Map;

public class SimpleBuilderParameterProvider extends AbstractParameterProvider {

    public SimpleBuilderParameterProvider(Map<String, PsiType> parameterCandidates) {
        super(null, parameterCandidates);
    }

    public SimpleBuilderParameterProvider(BuilderParameterProvider parameterSelector,
                                          LombokBuilderContextAnalyser lombokBuilderScopeParser) {
        super(parameterSelector, lombokBuilderScopeParser.getSourceParameterCandidates());
    }

    @Override
    protected String doSelect(String builderMethodName) {
        if (candidates.containsKey(builderMethodName)) {
            return builderMethodName;
        }
        for (Map.Entry<String, PsiType> entry: candidates.entrySet()) {
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

}
