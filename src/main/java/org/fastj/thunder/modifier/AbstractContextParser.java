package org.fastj.thunder.modifier;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public abstract class AbstractContextParser {

    protected final AnActionEvent anActionEvent;

    protected final Caret caret;

    protected final PsiJavaFile psiJavaFile;

    protected final PsiElement elementAtCaret;

    public AbstractContextParser(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
        caret = anActionEvent.getData(CommonDataKeys.CARET);
        assert caret != null;
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        psiJavaFile = (psiFile instanceof PsiJavaFile) ? (PsiJavaFile)  psiFile : null;
        assert psiJavaFile != null;
        elementAtCaret = psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
        assert elementAtCaret != null;
    }

    public Editor getEditor() {
        return anActionEvent.getData(CommonDataKeys.EDITOR);
    }

    public Project getProject() {
        return anActionEvent.getProject();
    }

    public PsiElement getElementAtCaret() {
        return elementAtCaret;
    }
}
