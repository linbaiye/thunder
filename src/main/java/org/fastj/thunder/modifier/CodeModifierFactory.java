package org.fastj.thunder.modifier;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.fastj.thunder.modifier.builder.BuilderCodeModifier;
import org.fastj.thunder.modifier.builder.BuilderParameterProviderFactory;
import org.fastj.thunder.modifier.builder.LombokBuilderContextParser;
import org.fastj.thunder.modifier.repository.RepositoryCodeModifier;
import org.fastj.thunder.modifier.repository.RepositoryContextParser;
import org.fastj.thunder.scope.*;

import java.util.Optional;

public class CodeModifierFactory {

    private final static CodeModifierFactory CODE_MODIFIER_FACTORY = new CodeModifierFactory();

    public static CodeModifierFactory getInstance() {
        return CODE_MODIFIER_FACTORY;
    }

    private ContextType matchType(ThunderEvent event) {
        ContextMatcher contextMatcher = ScopeMatcherFactory.getInstance().getOrCreate();
        return contextMatcher.match(event);
    }


    public Optional<? extends CodeModifier> create(ThunderEvent thunderEvent, ContextType contextType) {
        PsiFile psiFile = thunderEvent.getFile();
        if (!(psiFile instanceof PsiJavaFile)) {
            return Optional.empty();
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        switch (contextType) {
            case UNIT_TEST_CLASS:
                return UnitTestCodeModifier.create(psiJavaFile);
            case REPOSITORY_METHOD:
                RepositoryContextParser parser = new RepositoryContextParser(thunderEvent);
                return RepositoryCodeModifier.from(parser.findCurrentMethod(), parser.findEntityClass(),
                        parser.findDaoIdentifierNearFocusedElement(), parser.findDaoField());
            case BUILDER:
                LombokBuilderContextParser builderContextParser = new LombokBuilderContextParser(thunderEvent);
                return Optional.of(new BuilderCodeModifier(builderContextParser,
                        BuilderParameterProviderFactory.getInstance().create(builderContextParser)));
            default:
                return Optional.empty();
        }
    }

    public Optional<? extends CodeModifier> create(ThunderEvent thunderEvent) {
        return create(thunderEvent, matchType(thunderEvent));
    }
}
