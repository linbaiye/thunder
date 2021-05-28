package org.fastj.thunder.modifier;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.fastj.thunder.modifier.builder.BuilderCodeModifier;
import org.fastj.thunder.modifier.builder.BuilderParameterProviderFactory;
import org.fastj.thunder.modifier.builder.LombokBuilderScopeParser;
import org.fastj.thunder.modifier.builder.SimpleBuilderParameterProvider;
import org.fastj.thunder.modifier.persistence.RepositoryCodeModifier;
import org.fastj.thunder.modifier.persistence.RepositoryContextParser;
import org.fastj.thunder.scope.*;

import java.util.Optional;

public class CodeModifierFactory {

    private final static CodeModifierFactory CODE_MODIFIER_FACTORY = new CodeModifierFactory();

    public static CodeModifierFactory getInstance() {
        return CODE_MODIFIER_FACTORY;
    }

    private ScopeType matchType(ThunderEvent event) {
        ScopeMatcher scopeMatcher = ScopeMatcherFactory.getInstance().getOrCreate();
        return scopeMatcher.match(event);
    }


    public Optional<? extends CodeModifier> create(ThunderEvent thunderEvent, ScopeType scopeType) {
        PsiFile psiFile = thunderEvent.getFile();
        if (!(psiFile instanceof PsiJavaFile)) {
            return Optional.empty();
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        switch (scopeType) {
            case UNIT_TEST_CLASS:
                return UnitTestCodeModifier.create(psiJavaFile);
            case REPOSITORY:
                RepositoryContextParser parser = new RepositoryContextParser(thunderEvent);
                return RepositoryCodeModifier.from(parser.findCurrentMethod(), parser.findEntityClass(),
                        parser.findDaoIdentifierNearFocusedElement(), parser.findDaoField());
            case BUILDER:
                LombokBuilderScopeParser builderContextParser = new LombokBuilderScopeParser(thunderEvent);
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
