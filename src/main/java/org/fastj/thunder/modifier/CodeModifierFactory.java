package org.fastj.thunder.modifier;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.*;
import org.fastj.thunder.modifier.persistence.RepositoryCodeModifier;
import org.fastj.thunder.modifier.persistence.RepositoryStructureParser;

import java.util.Optional;

public class CodeModifierFactory {

    private final static CodeModifierFactory CODE_MODIFIER_FACTORY = new CodeModifierFactory();

    public static CodeModifierFactory getInstance() {
        return CODE_MODIFIER_FACTORY;
    }

    private boolean isUnitTest(PsiJavaFile psiJavaFile) {
        return psiJavaFile.getName().endsWith("UT.java") &&
                psiJavaFile.getContainingDirectory() != null &&
                psiJavaFile.getContainingDirectory().getVirtualFile().getPath().contains("src/test/java");
    }

    public Optional<? extends CodeModifier> create(AnActionEvent actionEvent) {
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof PsiJavaFile)) {
            return Optional.empty();
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        if (isUnitTest(psiJavaFile)) {
            return UnitTestCodeModifier.create(psiJavaFile);
        } else if (RepositoryStructureParser.isEventOccurredInRepositoryFile(actionEvent)) {
            RepositoryStructureParser parser = RepositoryStructureParser.from(actionEvent);
            return RepositoryCodeModifier.from(parser.findCurrentMethod(), parser.findEntityClass(), parser.findDaoIdentifierNearFocusedElement(), parser.findDaoField());
        }
        return Optional.empty();
    }
}
