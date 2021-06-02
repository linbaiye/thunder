package org.fastj.thunder.context;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * The context of event triggered.
 */
public interface ThunderEvent {

    default PsiElement getElementAtCaret() {
        return null;
    }

    default int getCaretOffset() {
        return -1;
    }

    /**
     * Gets the file currently editing.
     * @return
     */
    default PsiFile getFile() {
        return null;
    }

    default Project getProject() {
        return null;
    }

    default Editor getEditor() {
        return null;
    }
}
