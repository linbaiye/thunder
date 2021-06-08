package org.fastj.thunder.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class ThunderCompletionContributor extends CompletionContributor {

    private final static Set<String> REPOSITORY_METHOD_NAMES = new HashSet<>();

    static {
        REPOSITORY_METHOD_NAMES.add("selectOne");
        REPOSITORY_METHOD_NAMES.add("selectList");
        REPOSITORY_METHOD_NAMES.add("update");
    }

    private final static ElementPattern<? extends PsiElement> BUILDER_PATTERN =  PsiJavaPatterns.
                psiElement().afterLeaf(psiElement(JavaTokenType.DOT)
                    .afterSibling(psiElement(PsiMethodCallExpression.class)
                    .with(new PatternCondition<PsiMethodCallExpression>("Matching builder") {
                @Override
                public boolean accepts(@NotNull PsiMethodCallExpression psiMethodCallExpression, ProcessingContext context) {
                    PsiMethod method = psiMethodCallExpression.resolveMethod();
                    return method != null && "builder".equals(method.getName());
                }
            })));


    private final static ElementPattern<? extends PsiElement> MYBATIS_METHOD_PARAMETER_PATTERN = PlatformPatterns.
            psiElement(PsiIdentifier.class)
            .afterSibling(psiElement(PsiReferenceParameterList.class))
            .withAncestor(3, psiElement(PsiMethodCallExpression.class)
                    .with(new PatternCondition<PsiMethodCallExpression>("Matching dao method name") {
                        @Override
                        public boolean accepts(@NotNull PsiMethodCallExpression psiMethodCallExpression, ProcessingContext context) {
                            PsiMethod method = psiMethodCallExpression.resolveMethod();
                            return method != null && REPOSITORY_METHOD_NAMES.contains(method.getName());
                        }
                    })
            );


    private final static ElementPattern<? extends PsiElement> MOCK_CLASS_PATTERN =  PsiJavaPatterns.psiElement()
            .afterLeaf(psiElement(JavaTokenType.DOT)
            .afterSibling(psiElement(PsiJavaCodeReferenceElement.class)
                    .withChild(psiElement(PsiIdentifier.class).with(new PatternCondition<PsiIdentifier>("Matching mock class") {
                        @Override
                        public boolean accepts(@NotNull PsiIdentifier psiIdentifier, ProcessingContext context) {
                            PsiFile psiFile = psiIdentifier.getContainingFile();
                            if (!psiFile.getOriginalFile().getVirtualFile().getPath().contains("src/test/java")) {
                                return false;
                            }
                            char character = psiIdentifier.getText().charAt(0);
                            return character >= 'A' && character <= 'Z';
                        }
                    }))
            ));

    public ThunderCompletionContributor() {
        extend(CompletionType.BASIC, BUILDER_PATTERN, new BuilderCompletionProvider());
        extend(CompletionType.BASIC, MYBATIS_METHOD_PARAMETER_PATTERN, new MybatisCompletionProvider());
        extend(CompletionType.BASIC, MOCK_CLASS_PATTERN, new MockitoCompletionProvider());
    }
}
