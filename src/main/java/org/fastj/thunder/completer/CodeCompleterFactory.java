package org.fastj.thunder.completer;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.fastj.thunder.completer.builder.BuilderCodeCompleter;
import org.fastj.thunder.completer.builder.BuilderParameterProviderFactory;
import org.fastj.thunder.completer.builder.LombokBuilderContextAnalyser;
import org.fastj.thunder.completer.mockclass.MockClassCompleter;
import org.fastj.thunder.completer.mockclass.MockClassContextAnalyser;
import org.fastj.thunder.completer.mybatis.MybatisMethodParameterCompleter;
import org.fastj.thunder.completer.mybatis.RepositoryContextAnalyser;
import org.fastj.thunder.completer.validation.ValidationAnnotationCodeCompleter;
import org.fastj.thunder.completer.validation.ValidationAnnotationContextAnalyser;
import org.fastj.thunder.context.*;

import java.util.Optional;

public class CodeCompleterFactory {

    private final static CodeCompleterFactory CODE_MODIFIER_FACTORY = new CodeCompleterFactory();

    public static CodeCompleterFactory getInstance() {
        return CODE_MODIFIER_FACTORY;
    }

    public Optional<? extends CodeCompleter> create(ThunderEvent thunderEvent,
                                                    ContextType contextType) {
        PsiFile psiFile = thunderEvent.getFile();
        if (!(psiFile instanceof PsiJavaFile)) {
            return Optional.empty();
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        switch (contextType) {
            case INJECT_MOCKS:
                return UnitTestCodeCompleter.create(psiJavaFile);
            case MYBATIS_METHOD_PARAMETER:
                RepositoryContextAnalyser parser = new RepositoryContextAnalyser(thunderEvent);
                return Optional.of(new MybatisMethodParameterCompleter(parser));
            case MOCK_CLASS:
                return Optional.of(new MockClassCompleter(new MockClassContextAnalyser(thunderEvent)));
            case BUILDER:
                LombokBuilderContextAnalyser builderContextParser = new LombokBuilderContextAnalyser(thunderEvent);
                return Optional.of(new BuilderCodeCompleter(builderContextParser,
                        BuilderParameterProviderFactory.getInstance().create(builderContextParser)));
            case VALIDATOR_ANNOTATIONS:
                ValidationAnnotationContextAnalyser contextAnalyser = new ValidationAnnotationContextAnalyser(thunderEvent);
                return Optional.of(new ValidationAnnotationCodeCompleter(contextAnalyser));
            default:
                return Optional.empty();
        }
    }
}
