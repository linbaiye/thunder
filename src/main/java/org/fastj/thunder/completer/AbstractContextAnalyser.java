package org.fastj.thunder.completer;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.fastj.thunder.context.ThunderEvent;

public abstract class AbstractContextAnalyser {

    protected final ThunderEvent thunderEvent;

    public AbstractContextAnalyser(ThunderEvent thunderEvent) {
        this.thunderEvent = thunderEvent;
    }

    public Project getProject() {
        return thunderEvent.getProject();
    }

    public Editor getEditor() {
        return thunderEvent.getEditor();
    }

    public PsiElement getElementAtCaret() {
        return thunderEvent.getElementAtCaret();
    }
}
