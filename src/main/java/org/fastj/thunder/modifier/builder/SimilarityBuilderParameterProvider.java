package org.fastj.thunder.modifier.builder;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import org.fastj.thunder.until.NamingUtil;

import java.util.*;

public class SimilarityBuilderParameterProvider extends AbstractParameterProvider {

    public SimilarityBuilderParameterProvider(BuilderParameterProvider next,
                                              LombokBuilderScopeParser parser) {
        super(next, parser.getSourceParameterCandidates());
    }

    private SimilarityScore calculate(String methodName, String candidateName, String expression) {
        Set<Character> commonChars = new HashSet<>();
        int commonCharsCount = 0;
        char[] nameChars = methodName.toCharArray();
        for (char nameChar : nameChars) {
            commonChars.add(Character.toLowerCase(nameChar));
        }
        char[] candidateChars = candidateName.toCharArray();
        for (char candidateChar: candidateChars) {
            Character ch = Character.toLowerCase(candidateChar);
            if (commonChars.contains(ch)) {
                commonCharsCount++;
            }
        }
        int minLen = Math.min(methodName.length(), candidateName.length());
        return new SimilarityScore(expression, (double) commonCharsCount / minLen);
    }

    @Override
    protected String doSelect(String builderMethodName) {
        List<SimilarityScore> scoreList = new LinkedList<>();
        for (Map.Entry<String, PsiType> stringPsiTypeEntry : candidates.entrySet()) {
            String name = stringPsiTypeEntry.getKey();
            PsiType psiType = stringPsiTypeEntry.getValue();
            if (isJreType(psiType)) {
                scoreList.add(calculate(builderMethodName, name, name));
                continue;
            }
            PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
            if (psiClass == null) {
                continue;
            }
            PsiField[] fields = psiClass.getAllFields();
            for (PsiField field : fields) {
                scoreList.add(calculate(builderMethodName, field.getName(), name + ".get" + NamingUtil.capitalFirstChar(field.getName()) + "()"));
            }
        }
        if (scoreList.isEmpty()) {
            return null;
        }
        scoreList.sort(Comparator.comparing(o -> o.rate));
        return scoreList.get(0).expression;
    }


    private boolean isJreType(PsiType type) {
        return type.getCanonicalText().startsWith("java") ||
                type instanceof PsiPrimitiveType;
    }


    private static class SimilarityScore {

        private String expression;
        private Double rate;

        public SimilarityScore(String expression, Double rate) {
            this.expression = expression;
            this.rate = rate;
        }
    }
}
