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
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.fastj.thunder.logging.LoggerFactory;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.ActionThunderEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
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


    private PsiMethod findMethod(PsiElement element) {
        PsiMethod method = (element instanceof PsiMethod) ? (PsiMethod) element :
                PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method != null && method.getContainingClass() instanceof PsiAnonymousClass) {
            return findMethod(method.getParent());
        }
        return method;
    }


    private void dumpReference(PsiReferenceExpression referenceExpression) {
        referenceExpression.acceptChildren(new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                System.out.println(aClass.getClass() + ": " + aClass.getText());
            }

            @Override
            public void visitIdentifier(PsiIdentifier identifier) {
                System.out.println(identifier.getClass() + ": " + identifier.getText());
            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                System.out.println(expression.getClass() + ": " + expression.getText());
                PsiMethod psiMethod = expression.resolveMethod();
                if (psiMethod == null) {
                    return;
                }
                PsiType type = psiMethod.getReturnType();
                PsiClass builder = PsiTypesUtil.getPsiClass(type);
                if (builder != null) {
                    System.out.println(builder.getClass());
                }
            }

            @Override
            public void visitParameterList(PsiParameterList list) {
                System.out.println(list.getClass() + ": " + list.getText());
            }
        });
        PsiMethod method = findMethod(referenceExpression);
        if (method != null) {
//            new JavaRecursiveElementWalkingVisitor()
            method.acceptChildren(new JavaElementVisitor() {
                @Override
                public void visitCodeBlock(PsiCodeBlock block) {
                    block.acceptChildren(new JavaElementVisitor() {
                        @Override
                        public void visitForStatement(PsiForStatement statement) {
                            statement.acceptChildren(new JavaElementVisitor() {
                                @Override
                                public void visitElement(PsiElement element) {
                                    System.out.println(element.getClass());
                                }
                            });

                        }

                        @Override
                        public void visitForeachStatement(PsiForeachStatement statement) {
                            System.out.println(statement.getClass());
                        }

                        @Override
                        public void visitElement(PsiElement element) {
                            System.out.println(element.getClass());
                        }
                    });
                }

            });

            Collection<PsiParameter> parameterCollection = PsiTreeUtil.findChildrenOfType(method, PsiParameter.class);
            PsiParameter psiParameter;

            Collection<PsiLocalVariable> localVariables = PsiTreeUtil.findChildrenOfAnyType(method, PsiLocalVariable.class);
            PsiLocalVariable localVariable;
//            localVariable.getType();

//            method.acceptChildren(new JavaRecursiveElementVisitor() {
//                @Override
//                public void visitElement(PsiElement element) {
//                    System.out.println(element.getClass());
//                    super.visitElement(element);
//                }
//            });
        }
    }

    private void dump(PsiElement psiElement) {
        if (psiElement.getParent() instanceof PsiReferenceExpression) {
            dumpReference((PsiReferenceExpression) psiElement.getParent());
        } else if (psiElement.getParent() instanceof PsiForStatement ||
                psiElement.getParent() instanceof PsiLambdaExpression ) {
            psiElement.getParent().acceptChildren(new JavaElementVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    System.out.println(psiElement.getClass());
                }
            });
        }
    }


    private void parseElement(AnActionEvent event) {
        PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
        Caret caret = event.getData(CommonDataKeys.CARET);

        PsiElement pe = psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
        if (pe != null) {
            dump(pe);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LoggerFactory.setProject(event.getProject());
        Optional<? extends CodeCompleter> optional = CodeCompleterFactory.getInstance().create(new ActionThunderEvent(event));
        optional.ifPresent(CodeCompleter::tryComplete);
//        parseElement(event);
    }
}
