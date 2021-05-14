package org.fastj.thunder.modifier;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public abstract class ContextParser {

    private final AnActionEvent anActionEvent;

    private final Caret caret;

    private final PsiJavaFile psiJavaFile;

    private final PsiElement currentElement;

    public ContextParser(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
        this.caret = anActionEvent.getData(CommonDataKeys.CARET);
        assert caret != null;
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        psiJavaFile = (psiFile instanceof PsiJavaFile) ? (PsiJavaFile)  psiFile : null;
        assert psiJavaFile != null;
        currentElement = psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
    }
}
