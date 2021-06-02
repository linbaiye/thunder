package org.fastj.thunder.context;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public class UnitTestClassContextMatcher extends AbstractContextMatcher {

    public UnitTestClassContextMatcher(ContextMatcher next) {
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
    protected ContextType doMatch(ThunderEvent thunderEvent) {
        return isUnitTest(thunderEvent) ? ContextType.UNIT_TEST_CLASS : null;
    }
}
