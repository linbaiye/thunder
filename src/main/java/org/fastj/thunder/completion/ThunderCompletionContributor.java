package org.fastj.thunder.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.JavaTokenType;

public class ThunderCompletionContributor extends CompletionContributor {

    public ThunderCompletionContributor() {
        extend(CompletionType.BASIC, PsiJavaPatterns.psiElement(JavaTokenType.DOT), );
    }
}
