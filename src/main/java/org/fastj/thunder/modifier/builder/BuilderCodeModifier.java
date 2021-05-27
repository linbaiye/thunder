package org.fastj.thunder.modifier.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import org.fastj.thunder.modifier.CodeModifier;
import org.fastj.thunder.until.NamingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuilderCodeModifier implements CodeModifier {

    private final BuilderScopeParser contextParser;

    private final ParameterSelector parameterSelector;

    private Map<String, PsiType> parameterCandidates;

    public BuilderCodeModifier(BuilderScopeParser contextParser,
                               ParameterSelector parameterSelector) {
        this.contextParser = contextParser;
        this.parameterSelector = parameterSelector;
    }

    private String findGetter(String name, PsiClass from, String className) {
        PsiField field = from.findFieldByName(name, true);
        if (field != null) {
            return className + ".get" + NamingUtil.capitalFirstChar(name) + "()";
        }
        return null;
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
            String expression = parameterSelector.selectParameterExpression(psiMethod.getName());
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
            methodCallExpression.replace(element);
        });
    }


    private void chainBuilderMethods(String chosen) {
        if (!parameterCandidates.containsKey(chosen)) {
            return;
        }
        PsiClass builderClass = contextParser.getBuilderClass();
        if (builderClass == null || builderClass.getQualifiedName() == null) {
            return;
        }
        PsiClass resultClass = contextParser.getResultClass();
        if (resultClass == null || resultClass.getQualifiedName() == null) {
            return;
        }
        PsiType type = parameterCandidates.get(chosen);
        PsiClass sourceClass = PsiTypesUtil.getPsiClass(type);
        if (sourceClass == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder(resultClass.getQualifiedName());
        stringBuilder.append(".builder()\n");
        List<PsiMethod> methodList = contextParser.parseChainMethods();
        for (PsiMethod psiMethod : methodList) {
            String param = findGetter(psiMethod.getName(), sourceClass, chosen);
            if (param == null) {
                continue;
            }
            stringBuilder.append(".");
            stringBuilder.append(psiMethod.getName());
            stringBuilder.append("(");
            stringBuilder.append(param);
            stringBuilder.append(")\n");
        }
        stringBuilder.append(".build();");
        PsiElement element = PsiElementFactory.getInstance(contextParser.getProject()).createStatementFromText(stringBuilder.toString(), null);
        WriteCommandAction.runWriteCommandAction(contextParser.getProject(), "", "", () -> {
            contextParser.getElementAtCaret().replace(element);
        });
    }

    private void buildPopupMenu() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String label : parameterCandidates.keySet()) {
            model.addElement(label);
        }
        Editor editor = contextParser.getEditor();
        if (editor != null) {
            JBPopup jbPopup = JBPopupFactory.getInstance()
                    .createPopupChooserBuilder(new ArrayList<>(parameterCandidates.keySet()))
                    .setItemChosenCallback(this::chainBuilderMethods)
                    .setTitle("Choose Source Class")
                    .setMinSize(new Dimension(150, 30))
                    .createPopup();
            jbPopup.showInBestPositionFor(editor);
        }
    }


    @Override
    public void tryModify() {
        chainBuilderMethods();
//        buildPopupMenu();
    }
}
