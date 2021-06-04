package org.fastj.thunder.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.CompletionThunderEvent;
import org.fastj.thunder.context.ContextType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MockitoCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context,
                                  @NotNull CompletionResultSet result) {
        result.addElement(LookupElementBuilder.create("mock").withInsertHandler(
                (context1, item) -> {
                    Optional<? extends CodeCompleter> optionalCodeModifier = CodeCompleterFactory.getInstance().create(new CompletionThunderEvent(context1),
                            ContextType.MOCK_CLASS);
                    optionalCodeModifier.ifPresent(CodeCompleter::tryComplete);
                }
        ));
    }
}
