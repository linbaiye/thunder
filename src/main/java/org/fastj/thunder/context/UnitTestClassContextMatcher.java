package org.fastj.thunder.context;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import java.util.Set;

public class UnitTestClassContextMatcher implements ContextMatcher {

    private boolean isWithinUnitTestClass(ThunderEvent thunderEvent) {
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
    public void addIfMatch(ThunderEvent event,
                           Set<ContextType> result) {
        if (isWithinUnitTestClass(event)) {
            result.add(ContextType.INJECT_MOCKS);
        }
    }
}
