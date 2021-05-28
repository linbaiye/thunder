package org.fastj.thunder.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class ThunderCompletionContributor extends CompletionContributor {

    private final static ElementPattern<? extends PsiElement> BUILDER_PATTERN =  PlatformPatterns.
                psiElement().afterLeaf(psiElement(JavaTokenType.DOT)
                    .afterSibling(psiElement(PsiMethodCallExpression.class)
                    .with(new PatternCondition<PsiMethodCallExpression>("Matching builder") {
                @Override
                public boolean accepts(@NotNull PsiMethodCallExpression psiMethodCallExpression, ProcessingContext context) {
                    PsiMethod method = psiMethodCallExpression.resolveMethod();
                    return method != null && "builder".equals(method.getName());
                }
            })));


    public ThunderCompletionContributor() {
        extend(CompletionType.BASIC, BUILDER_PATTERN, new BuilderCompletionProvider());
    }
}
