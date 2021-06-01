package org.fastj.thunder.modifier.builder;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.fastj.thunder.until.NamingUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SimilarityBuilderParameterProvider extends AbstractParameterProvider {

    private final JaroWinklerSimilarity similarityCalculator;

    private final static Double THRESHOLD = 0.7;

    public SimilarityBuilderParameterProvider(BuilderParameterProvider next,
                                              LombokBuilderContextParser parser) {
        super(next, parser.getSourceParameterCandidates());
        similarityCalculator = new JaroWinklerSimilarity();
    }

    @Override
    protected String doSelect(String builderMethodName) {
        List<JaroWinklerSimilarityScore> scoreList = new LinkedList<>();
        for (Map.Entry<String, PsiType> stringPsiTypeEntry : candidates.entrySet()) {
            String name = stringPsiTypeEntry.getKey();
            PsiType psiType = stringPsiTypeEntry.getValue();
            PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
            if (psiClass == null) {
                double score = similarityCalculator.apply(builderMethodName, name);
                scoreList.add(new JaroWinklerSimilarityScore(name, score));
                continue;
            }
            PsiField[] fields = psiClass.getAllFields();
            for (PsiField field : fields) {
                double fieldScore = similarityCalculator.apply(builderMethodName, field.getName());
                if (fieldScore < THRESHOLD) {
                    continue;
                }
                String exp = name + ".get" + NamingUtil.capitalFirstChar(field.getName()) + "()";
                double expScore = similarityCalculator.apply(builderMethodName, exp);
                scoreList.add(new JaroWinklerSimilarityScore(exp, Math.max(expScore, fieldScore)));
            }
        }
        if (scoreList.isEmpty()) {
            return null;
        }
        Collections.sort(scoreList);
        return scoreList.get(0).score >= THRESHOLD ? scoreList.get(0).expression : null;
    }

    private static class JaroWinklerSimilarityScore implements Comparable<JaroWinklerSimilarityScore> {
        private final String expression;
        private final Double score;

        public JaroWinklerSimilarityScore(String expression, Double score) {
            this.expression = expression;
            this.score = score;
        }

        @Override
        public int compareTo(@NotNull SimilarityBuilderParameterProvider.JaroWinklerSimilarityScore o) {
            return o.score.compareTo(this.score);
        }
    }
}
