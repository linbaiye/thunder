package org.fastj.thunder.scope;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public class RepositoryScopeMatcher extends AbstractScopeMatcher {

    public RepositoryScopeMatcher(ScopeMatcher next) {
        super(next);
    }

    private boolean isInsideRepositoryScope(ThunderEvent thunderEvent) {
        PsiFile psiFile = thunderEvent.getFile();
        if (!(psiFile instanceof PsiJavaFile)) {
            return false;
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        return (psiJavaFile.getName().endsWith("Repository.java") ||
                psiJavaFile.getName().endsWith("RepositoryImpl.java")) &&
                psiJavaFile.getClasses().length == 1 &&
                psiJavaFile.getContainingDirectory().getVirtualFile().getPath().contains("src/main/java") &&
                psiJavaFile.getPackageName().endsWith(".repository");
    }

    @Override
    protected ScopeType doMatch(ThunderEvent thunderEvent) {
        return isInsideRepositoryScope(thunderEvent) ? ScopeType.REPOSITORY : null;
    }
}
