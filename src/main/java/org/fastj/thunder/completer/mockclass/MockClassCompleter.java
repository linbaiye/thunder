package org.fastj.thunder.completer.mockclass;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.until.NamingUtil;

public class MockClassCompleter implements CodeCompleter {

    private final MockClassContextAnalyser contextAnalyser;

    public MockClassCompleter(MockClassContextAnalyser contextAnalyser) {
        this.contextAnalyser = contextAnalyser;
    }

    private PsiElement buildMockExpression(PsiElement beingReplacedElement,
                                           PsiElement context,
                                           PsiClass beingMockedClass) {
        if (beingReplacedElement instanceof PsiReferenceExpression) {
            return PsiElementFactory.getInstance(contextAnalyser.getProject()).createStatementFromText(
                     "org.mockito.Mockito.mock(" +
                            beingMockedClass.getName() + ".class)"
                    , context);
        } else {
            return PsiElementFactory.getInstance(contextAnalyser.getProject()).createStatementFromText(
                    beingMockedClass.getQualifiedName() + " " + NamingUtil.nameClass(beingMockedClass.getName()) + " = org.mockito.Mockito.mock(" +
                            beingMockedClass.getName() + ".class);"
                    , context);
        }
    }

    private boolean isCompletable() {
        return contextAnalyser.getElementToReplace() != null &&
                contextAnalyser.getMockingClass() != null;
    }

    @Override
    public void tryComplete() {
        if (isCompletable()) {
            PsiClass psiClass = contextAnalyser.getMockingClass();
            PsiElement toReplace = contextAnalyser.getElementToReplace();
            // FIXME: NPE when triggered as a parameter.
            PsiElement expression = buildMockExpression(toReplace, toReplace.getContext(), psiClass);
            WriteCommandAction.runWriteCommandAction(contextAnalyser.getProject(), () -> {
                PsiElement mockExpression = JavaCodeStyleManager.getInstance(contextAnalyser.getProject()).shortenClassReferences(expression);
                toReplace.replace(mockExpression);
            });
        }
    }
}
