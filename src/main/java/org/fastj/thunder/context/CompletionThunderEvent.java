package org.fastj.thunder.context;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class CompletionThunderEvent implements ThunderEvent {

    private final InsertionContext insertionContext;

    public CompletionThunderEvent(InsertionContext insertionContext) {
        this.insertionContext = insertionContext;
    }

    @Override
    public PsiElement getElementAtCaret() {
        return getFile().findElementAt(getCaretOffset());
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
