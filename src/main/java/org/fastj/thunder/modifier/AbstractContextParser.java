package org.fastj.thunder.modifier;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.fastj.thunder.scope.ThunderEvent;

public abstract class AbstractContextParser {

    protected final ThunderEvent thunderEvent;

    public AbstractContextParser(ThunderEvent thunderEvent) {
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
