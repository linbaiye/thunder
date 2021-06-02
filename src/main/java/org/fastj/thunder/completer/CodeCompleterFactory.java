package org.fastj.thunder.completer;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.fastj.thunder.completer.builder.BuilderCodeCompleter;
import org.fastj.thunder.completer.builder.BuilderParameterProviderFactory;
import org.fastj.thunder.completer.builder.LombokBuilderContextAnalyser;
import org.fastj.thunder.completer.mybatis.MybatisMethodParameterCompleter;
import org.fastj.thunder.completer.mybatis.RepositoryContextAnalyser;
import org.fastj.thunder.context.*;

import java.util.Optional;

public class CodeCompleterFactory {

    private final static CodeCompleterFactory CODE_MODIFIER_FACTORY = new CodeCompleterFactory();

    public static CodeCompleterFactory getInstance() {
        return CODE_MODIFIER_FACTORY;
    }

    private ContextType matchType(ThunderEvent event) {
        ContextMatcher contextMatcher = ContextMatcherFactory.getInstance().getOrCreate();
        return contextMatcher.match(event);
    }

    public Optional<? extends CodeCompleter> create(ThunderEvent thunderEvent, ContextType contextType) {
        PsiFile psiFile = thunderEvent.getFile();
        if (!(psiFile instanceof PsiJavaFile)) {
            return Optional.empty();
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        switch (contextType) {
            case UNIT_TEST_CLASS:
                return UnitTestCodeCompleter.create(psiJavaFile);
            case MYBATIS_METHOD_PARAMETER:
                RepositoryContextAnalyser parser = new RepositoryContextAnalyser(thunderEvent);
                return Optional.of(new MybatisMethodParameterCompleter(parser));
            case BUILDER:
                LombokBuilderContextAnalyser builderContextParser = new LombokBuilderContextAnalyser(thunderEvent);
                return Optional.of(new BuilderCodeCompleter(builderContextParser,
                        BuilderParameterProviderFactory.getInstance().create(builderContextParser)));
            default:
                return Optional.empty();
        }
    }

    public Optional<? extends CodeCompleter> create(ThunderEvent thunderEvent) {
        return create(thunderEvent, matchType(thunderEvent));
    }
}
