package org.fastj.thunder;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import org.fastj.thunder.logging.LoggerFactory;
import org.fastj.thunder.modifier.CodeModifier;
import org.fastj.thunder.modifier.CodeModifierFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

public class PopupDialogAction extends AnAction {


    @Override
    public void update(@NotNull AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    private void testmenu(AnActionEvent event) {
        String labels[] = new String[] {"Query", "Inject mocks", "Convert object", "Validation"};
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String label : labels) {
            model.addElement(label);
        }
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            JBPopup jbPopup = JBPopupFactory.getInstance()
                    .createPopupChooserBuilder(Arrays.asList(labels))
                    .setItemChosenCallback(s -> {
                        System.out.println(s + " was selected.");
                    })
                    .setTitle("Choose Action")
                    .setMinSize(new Dimension(150, 30))
                    .createPopup();
            jbPopup.showInBestPositionFor(editor);
        }
    }

    private void parser(PsiElement psiElement) {
        if (psiElement instanceof PsiIdentifier) {
            PsiIdentifier psiIdentifier = (PsiIdentifier) psiElement;
            PsiElement parent = psiIdentifier.getParent();
        }
    }

    private void dump(PsiIdentifier psiIdentifier) {
        if (!(psiIdentifier.getParent() instanceof PsiReferenceExpression)) {
            return;
        }
        PsiReferenceExpression referenceExpression = (PsiReferenceExpression) psiIdentifier.getParent();
        referenceExpression.accept(new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                System.out.println(element.getClass() + ": " + element.getText());
            }
        });
    }


    private void dump(PsiWhiteSpace psiWhiteSpace) {
        if (!(psiWhiteSpace.getParent() instanceof PsiReferenceExpression)) {
            return;
        }
        PsiReferenceExpression referenceExpression = (PsiReferenceExpression) psiWhiteSpace.getParent();
        referenceExpression.accept(new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                System.out.println(element.getClass() + ": " + element.getText());
            }
        });
    }


    private void parseElement(AnActionEvent event) {
        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement != null) {
            System.out.println(psiElement);
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
        Caret caret = event.getData(CommonDataKeys.CARET);

        PsiElement pe = psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
        if (pe != null) {
            System.out.println(pe);
            if (pe instanceof PsiIdentifier) {
                dump((PsiIdentifier) pe);
            } else if (pe instanceof PsiWhiteSpace) {
                dump((PsiWhiteSpace) pe);
            }
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LoggerFactory.setProject(event.getProject());
        Optional<? extends CodeModifier> optional = CodeModifierFactory.getInstance().create(event);
        optional.ifPresent(CodeModifier::tryModify);
        parseElement(event);
    }
}
