package org.fastj.thunder.modifier;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.fastj.thunder.modifier.builder.BuilderCodeModifier;
import org.fastj.thunder.modifier.builder.BuilderScopeParser;
import org.fastj.thunder.modifier.builder.SimpleParameterSelector;
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

    public Optional<? extends CodeModifier> create(ThunderEvent thunderEvent) {
        PsiFile psiFile = thunderEvent.getFile();
        if (!(psiFile instanceof PsiJavaFile)) {
            return Optional.empty();
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        switch (matchType(thunderEvent)) {
            case UNIT_TEST_CLASS:
                return UnitTestCodeModifier.create(psiJavaFile);
            case REPOSITORY:
                RepositoryContextParser parser = new RepositoryContextParser(thunderEvent);
                return RepositoryCodeModifier.from(parser.findCurrentMethod(), parser.findEntityClass(),
                        parser.findDaoIdentifierNearFocusedElement(), parser.findDaoField());
            case BUILDER:
                BuilderScopeParser builderContextParser = new BuilderScopeParser(thunderEvent);
                return Optional.of(new BuilderCodeModifier(builderContextParser,
                        new SimpleParameterSelector(builderContextParser.getSourceParameterCandidates())));
            default:
                return Optional.empty();
        }
    }
}
