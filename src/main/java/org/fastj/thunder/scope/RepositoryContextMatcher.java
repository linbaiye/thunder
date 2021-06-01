package org.fastj.thunder.scope;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public class RepositoryContextMatcher extends AbstractContextMatcher {

    public RepositoryContextMatcher(ContextMatcher next) {
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
    protected ContextType doMatch(ThunderEvent thunderEvent) {
        return isInsideRepositoryScope(thunderEvent) ? ContextType.REPOSITORY_METHOD : null;
    }
}
