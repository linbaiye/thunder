package org.fastj.thunder.completer.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import org.fastj.thunder.completer.CodeCompleter;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class BuilderCodeCompleter implements CodeCompleter {

    private final LombokBuilderContextAnalyser contextParser;

    private final BuilderParameterProvider builderParameterProvider;

    public BuilderCodeCompleter(LombokBuilderContextAnalyser contextParser,
                                BuilderParameterProvider builderParameterProvider) {
        this.contextParser = contextParser;
        this.builderParameterProvider = builderParameterProvider;
    }

    private void chainBuilderMethods() {
        PsiMethodCallExpression methodCallExpression = contextParser.getMethodCallExpression();
        if (methodCallExpression == null) {
            return;
        }
        PsiClass resultClass = contextParser.getResultClass();
        if (resultClass == null || resultClass.getQualifiedName() == null) {
            return;
        }
        List<PsiMethod> methodList = contextParser.parseChainMethods();
        if (methodList.isEmpty()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder(resultClass.getQualifiedName());
        stringBuilder.append(".builder()\n");
        for (PsiMethod psiMethod : methodList) {
            String expression = builderParameterProvider.provideParameterExpression(psiMethod.getName());
            if (expression == null) {
                continue;
            }
            stringBuilder.append(".");
            stringBuilder.append(psiMethod.getName());
            stringBuilder.append("(");
            stringBuilder.append(expression);
            stringBuilder.append(")\n");
        }
        stringBuilder.append(contextParser.isBuilderMethodContainedInLambda() ? ".build()": ".build();");
        PsiElement element = PsiElementFactory.getInstance(contextParser.getProject()).createStatementFromText(stringBuilder.toString(), null);
        WriteCommandAction.runWriteCommandAction(contextParser.getProject(), "", "", () -> {
            PsiElement replaced = methodCallExpression.replace(element);
            if (replaced.getNextSibling() instanceof PsiJavaToken) {
                // Any more graceful method to remove the insertion text?
                PsiJavaToken token = (PsiJavaToken) replaced.getNextSibling();
                if (".".equals(token.getText())) {
                    token.delete();
                }
            }
        });
    }


    private void buildPopupMenu() {
        Editor editor = contextParser.getEditor();
        if (editor != null) {
            JBPopup jbPopup = JBPopupFactory.getInstance()
                    .createPopupChooserBuilder(Arrays.asList("Hello", "World"))
                    .setItemChosenCallback(System.out::println)
                    .setTitle("Choose Source Class")
                    .setMinSize(new Dimension(150, 30))
                    .createPopup();
            jbPopup.showInBestPositionFor(editor);
        }
    }


    @Override
    public void tryComplete() {
        chainBuilderMethods();
    }
}
