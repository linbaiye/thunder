package org.fastj.thunder.scope;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;

public class TestThunderEvent implements ThunderEvent {

    private final JavaCodeInsightTestFixture fixture;

    public TestThunderEvent(JavaCodeInsightTestFixture fixture) {
        this.fixture = fixture;
    }

    @Override
    public PsiElement getElementAtCaret() {
        return getFile().findElementAt(getCaretOffset());
    }

    @Override
    public int getCaretOffset() {
        return fixture.getCaretOffset();
    }

    @Override
    public PsiFile getFile() {
        return fixture.getFile();
    }

    @Override
    public Project getProject() {
        return fixture.getProject();
    }

    @Override
    public Editor getEditor() {
        return fixture.getEditor();
    }
}
