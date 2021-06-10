package org.fastj.thunder.completer.mockclass;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.until.NamingUtil;

public class MockClassCompleter implements CodeCompleter {

    private final MockClassContextAnalyser contextAnalyser;

    public MockClassCompleter(MockClassContextAnalyser contextAnalyser) {
        this.contextAnalyser = contextAnalyser;
    }

    @Override
    public void tryComplete() {
        PsiClass psiClass = contextAnalyser.getMockingClass();
        PsiElement toReplace = contextAnalyser.getElementToReplace();
        // FIXME: NPE when triggered as a parameter.
        PsiElement expression = PsiElementFactory.getInstance(contextAnalyser.getProject()).createStatementFromText(
                psiClass.getQualifiedName() + " " + NamingUtil.nameClass(psiClass.getName()) + " = org.mockito.Mockito.mock(" +
                        psiClass.getName() + ".class);"
        , toReplace.getContext());
        WriteCommandAction.runWriteCommandAction(contextAnalyser.getProject(), () -> {
            PsiElement mockExpression = JavaCodeStyleManager.getInstance(contextAnalyser.getProject()).shortenClassReferences(expression);
            toReplace.replace(mockExpression);
        });
    }
}
