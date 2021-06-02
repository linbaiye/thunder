package org.fastj.thunder.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.CompletionThunderEvent;
import org.fastj.thunder.context.ContextType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BuilderCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.addElement(LookupElementBuilder.create("thunder")
                .withInsertHandler((insertionContext, element) -> {
                    Optional<? extends CodeCompleter> optionalCodeModifier = CodeCompleterFactory.getInstance().create(new CompletionThunderEvent(insertionContext),
                            ContextType.BUILDER);
                    optionalCodeModifier.ifPresent( e -> {
                        PsiElement thunderString = insertionContext.getFile().findElementAt(insertionContext.getStartOffset());
                        if (thunderString != null) {
                            WriteCommandAction.runWriteCommandAction(insertionContext.getProject(), "", "", thunderString::delete);
                        }
                        e.tryComplete();
                    });
                })
        );
    }
}
