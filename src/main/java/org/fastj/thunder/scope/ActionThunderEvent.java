package org.fastj.thunder.scope;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class ActionThunderEvent implements ThunderEvent {

    private final AnActionEvent anActionEvent;

    public ActionThunderEvent(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
    }

    @Override
    public PsiElement getElementAtCaret() {
        return getFile().findElementAt(getCaretOffset());
    }

    @Override
    public int getCaretOffset() {
        Caret caret = anActionEvent.getData(CommonDataKeys.CARET);
        if (caret != null) {
            return caret.getCaretModel().getOffset();
        }
        return -1;
    }

    @Override
    public PsiFile getFile() {
        return anActionEvent.getData(CommonDataKeys.PSI_FILE);
    }

    @Override
    public Project getProject() {
        return anActionEvent.getProject();
    }

    @Override
    public Editor getEditor() {
        return anActionEvent.getData(CommonDataKeys.EDITOR);
    }
}
