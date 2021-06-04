package org.fastj.thunder.context;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class CompletionThunderEvent implements ThunderEvent {

    private final InsertionContext insertionContext;

    private final PsiElement elementAtCaret;

    public CompletionThunderEvent(InsertionContext insertionContext) {
        this.insertionContext = insertionContext;
        elementAtCaret = getFile().findElementAt(getCaretOffset());
    }

    @Override
    public PsiElement getElementAtCaret() {
        return elementAtCaret;
    }

    @Override
    public int getCaretOffset() {
        return insertionContext.getEditor().getCaretModel().getOffset();
    }

    @Override
    public PsiFile getFile() {
        return insertionContext.getFile();
    }

    @Override
    public Project getProject() {
        return insertionContext.getProject();
    }

    @Override
    public Editor getEditor() {
        return insertionContext.getEditor();
    }
}
