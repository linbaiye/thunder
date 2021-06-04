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

    private final static Set<String> REPOSITORY_PREFIX = new HashSet<>();

    static {
        REPOSITORY_METHOD_NAMES.add("selectOne");
        REPOSITORY_METHOD_NAMES.add("selectList");
        REPOSITORY_METHOD_NAMES.add("update");
        REPOSITORY_PREFIX.add("l");
        REPOSITORY_PREFIX.add("la");
        REPOSITORY_PREFIX.add("lam");
        REPOSITORY_PREFIX.add("lamd");
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
//                    .withChild(psiElement(PsiReferenceExpression.class)
//                            .with(new PatternCondition<PsiReferenceExpression>("Matching dao class") {
//                        @Override
//                        public boolean accepts(@NotNull PsiReferenceExpression referenceExpression, ProcessingContext context) {
//                            PsiExpression expression = referenceExpression.getQualifierExpression();
//                            if (expression == null) {
//                                return false;
//                            }
//                            PsiClass psiClass = PsiTypesUtil.getPsiClass(expression.getType());
//                            if (psiClass == null) {
//                                return false;
//                            }
//                            PsiAnnotation[] annotations = psiClass.getAnnotations();
//                            for (PsiAnnotation annotation : annotations) {
//                                if ("repository".equalsIgnoreCase(annotation.getText())) {
//                                    return true;
//                                }
//                            }
//                            return false;
//                        }
//                    }))
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
                    .with(new PatternCondition<PsiJavaCodeReferenceElement>("Matching mock class") {
                        @Override
                        public boolean accepts(@NotNull PsiJavaCodeReferenceElement psiJavaCodeReferenceElement, ProcessingContext context) {
                            PsiFile psiFile = psiJavaCodeReferenceElement.getContainingFile();
                            return psiFile.getOriginalFile().getVirtualFile().getPath().contains("src/test/java");
                        }
                    })));

    public ThunderCompletionContributor() {
        extend(CompletionType.BASIC, BUILDER_PATTERN, new BuilderCompletionProvider());
        extend(CompletionType.BASIC, MYBATIS_METHOD_PARAMETER_PATTERN, new MybatisCompletionProvider());
        extend(CompletionType.BASIC, MOCK_CLASS_PATTERN, new MockitoCompletionProvider());
    }
}
