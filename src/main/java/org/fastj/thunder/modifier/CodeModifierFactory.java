package org.fastj.thunder.modifier;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.*;
import org.fastj.thunder.modifier.builder.BuilderCodeModifier;
import org.fastj.thunder.modifier.builder.BuilderContextParser;
import org.fastj.thunder.modifier.persistence.RepositoryCodeModifier;
import org.fastj.thunder.modifier.persistence.RepositoryContextParser;
import org.fastj.thunder.scope.DefaultScopeMatcher;
import org.fastj.thunder.scope.Scope;

import java.util.Optional;

public class CodeModifierFactory {

    private final static CodeModifierFactory CODE_MODIFIER_FACTORY = new CodeModifierFactory();

    public static CodeModifierFactory getInstance() {
        return CODE_MODIFIER_FACTORY;
    }

    public Optional<? extends CodeModifier> create(AnActionEvent actionEvent) {
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof PsiJavaFile)) {
            return Optional.empty();
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        Scope scope = new DefaultScopeMatcher().match(actionEvent);
        switch (scope) {
            case UNIT_TEST_CLASS:
                return UnitTestCodeModifier.create(psiJavaFile);
            case REPOSITORY:
                RepositoryContextParser parser = RepositoryContextParser.from(actionEvent);
                return RepositoryCodeModifier.from(parser.findCurrentMethod(), parser.findEntityClass(),
                        parser.findDaoIdentifierNearFocusedElement(), parser.findDaoField());
            case BUILDER:
                BuilderContextParser builderContextParser = new BuilderContextParser(actionEvent);
                return Optional.of(new BuilderCodeModifier(builderContextParser));
            default:
                return Optional.empty();
        }
    }
}
