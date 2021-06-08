package org.fastj.thunder.context;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import java.util.Set;

public class ValidationContextMatcher implements ContextMatcher {

    @Override
    public void addIfMatch(ThunderEvent event,
                           Set<ContextType> result) {
        PsiFile file = event.getFile();
        Editor editor = event.getEditor();
        if (editor == null || !(file instanceof PsiJavaFile)) {
            return;
        }
        if (!file.getOriginalFile()
                .getVirtualFile()
                .getPath()
                .contains("src/test/java")) {
            result.add(ContextType.VALIDATOR_ANNOTATIONS);
        }
    }
}
