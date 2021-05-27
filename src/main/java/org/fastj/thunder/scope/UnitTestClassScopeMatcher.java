package org.fastj.thunder.scope;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public class UnitTestClassScopeMatcher extends AbstractScopeMatcher {

    public UnitTestClassScopeMatcher(ScopeMatcher next) {
        super(next);
    }

    private boolean isUnitTest(ThunderEvent thunderEvent) {
        PsiFile psiFile = thunderEvent.getFile();
        if (!(psiFile instanceof PsiJavaFile)) {
            return false;
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        return psiJavaFile.getName().endsWith("UT.java") &&
                psiJavaFile.getContainingDirectory() != null &&
                psiJavaFile.getContainingDirectory().getVirtualFile().getPath().contains("src/test/java");
    }

    @Override
    protected ScopeType doMatch(ThunderEvent thunderEvent) {
        return isUnitTest(thunderEvent) ? ScopeType.UNIT_TEST_CLASS : null;
    }
}
