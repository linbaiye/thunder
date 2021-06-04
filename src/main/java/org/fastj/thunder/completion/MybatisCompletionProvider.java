package org.fastj.thunder.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.CompletionThunderEvent;
import org.fastj.thunder.context.ContextType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MybatisCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context,
                                  @NotNull CompletionResultSet result) {
        result.addElement(LookupElementBuilder.create("lambda")
                .withTypeText("new QueryWrapper<>()", null, true)
                .withInsertHandler((insertionContext, element) -> {
                    Optional<? extends CodeCompleter> optionalCodeModifier = CodeCompleterFactory.getInstance().create(new CompletionThunderEvent(insertionContext),
                            ContextType.MYBATIS_METHOD_PARAMETER);
                    optionalCodeModifier.ifPresent( e -> {
                        e.tryComplete();
                    });
                })
        );
    }
}
