package org.fastj.thunder.modifier.builder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import org.fastj.thunder.modifier.CodeModifier;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuilderCodeModifier implements CodeModifier {

    private final BuilderContextParser contextParser;

    private Map<String, PsiType> parameterCandidates;

    public BuilderCodeModifier(BuilderContextParser contextParser) {
        this.contextParser = contextParser;
    }


    private void chainBuilderMethods() {
        PsiClass resultClass = contextParser.getBuilderClass();
        if (resultClass == null || resultClass.getQualifiedName() == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder(resultClass.getQualifiedName());
        stringBuilder.append(".builder()");
        List<PsiMethod> methodList = contextParser.parseChainMethods();
        for (PsiMethod psiMethod : methodList) {
            stringBuilder.append(".");
            stringBuilder.append(psiMethod.getName());
            stringBuilder.append("()\n");
        }
        stringBuilder.append(".build();");
        PsiElementFactory.getInstance(contextParser.getProject()).createStatementFromText(stringBuilder.toString(), null);
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
                    .setItemChosenCallback(s -> {
                        System.out.println(s + " was selected.");
                    })
                    .setTitle("Choose Source Class")
                    .setMinSize(new Dimension(150, 30))
                    .createPopup();
            jbPopup.showInBestPositionFor(editor);
        }
    }


    @Override
    public void tryModify() {
        parameterCandidates = contextParser.parseBuilderSourceParameterCandidates();
        if (parameterCandidates.isEmpty()) {
            return;
        }
        buildPopupMenu();
    }
}
