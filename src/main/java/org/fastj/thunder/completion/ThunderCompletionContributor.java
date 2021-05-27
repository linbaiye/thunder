package org.fastj.thunder.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class ThunderCompletionContributor extends CompletionContributor {



    public ThunderCompletionContributor() {

        extend(CompletionType.BASIC, PlatformPatterns.or(psiElement().afterLeaf(".")
//                ,psiElement().afterLeaf(psiElement(JavaTokenType.DOT).afterSibling(psiElement(PsiMethodCallExpression.class)))
                )
                , new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                result.addElement(LookupElementBuilder.create("thunder").
                        withInsertHandler(new InsertHandler<LookupElement>() {
                            @Override
                            public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
                                System.out.println("selected");
                            }
                        }));
            }
        });
    }
}
