package org.example.thunder;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiCodeFragmentImpl;
import com.intellij.psi.impl.source.tree.PsiPlainTextImpl;
import com.intellij.psi.impl.source.tree.java.PsiBlockStatementImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PopupDialogAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    private void print(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("org.example.thunder");
        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "MyPlugin Output", false);
        toolWindow.getContentManager().addContent(content);
        consoleView.print("Hello from MyPlugin!", ConsoleViewContentType.NORMAL_OUTPUT);
    }

    private void createTestMethod(PsiFile psiFile) {
        if (!(psiFile instanceof PsiJavaFile)) {
            return;
        }
        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
        if (javaFile.getClasses().length > 1) {
            return;
        }
        PsiClass psiClass = javaFile.getClasses()[0];
        PsiMethod[] methods = psiClass.findMethodsByName("helloWorld", false);
        for (PsiMethod method : methods) {
            if ("helloWorld".equals(method.getName())) {
                return;
            }
        }
        PsiField field;
        Project project = psiFile.getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiMethod method = factory.createMethod("helloWorld", PsiType.getJavaLangString(PsiManager.getInstance(project), GlobalSearchScope.allScope(project)));
        PsiCodeBlock codeBlock = method.getBody();
        if (codeBlock == null) {
            return;
        }
        PsiStatement psiStatement = factory.createStatementFromText("if (1==1){return \"hello\";} else {return \"bad\";}", codeBlock);
        codeBlock.add(psiStatement);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            psiClass.add(method);
        });
        print(project);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        if (psiFile != null) {
            Messages.showInfoMessage("Adding method to " + psiFile.getName(), "Current File Name");
            createTestMethod(psiFile);
        }
//        Optional<JavaMethod> optionalMethod = JavaMethod.fromContainingMethodOrSelf(psiElement);
//        optionalMethod.ifPresent(e -> {
//            StringBuilder stringBuilder = new StringBuilder();
//            Messages.showInfoMessage(stringBuilder.toString(), "Arguments");
//        });
    }
}
