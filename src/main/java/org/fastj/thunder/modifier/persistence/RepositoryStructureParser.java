package org.fastj.thunder.modifier.persistence;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;


public class RepositoryStructureParser {

    private final AnActionEvent event;

    private final Caret caret;

    private final PsiJavaFile psiJavaFile;

    private final PsiClass psiClass;

    private RepositoryStructureParser(AnActionEvent event) {
        this.event = event;
        this.caret = event.getData(CommonDataKeys.CARET);
        this.psiJavaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
        this.psiClass = this.psiJavaFile.getClasses()[0];
    }

    /**
     * The method where the caret is located.
     * @return
     */
    public PsiMethod findCurrentMethod() {
        if (caret == null) {
            return null;
        }
        PsiElement pe = psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
        return findMethod(pe);
    }

    private PsiElement getFocusedElement() {
        if (caret == null) {
            return null;
        }
        return psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
    }

    private PsiMethod findMethod(PsiElement element) {
        PsiMethod method = (element instanceof PsiMethod) ? (PsiMethod) element :
                PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method != null && method.getContainingClass() instanceof PsiAnonymousClass) {
            return findMethod(method.getParent());
        }
        return method;
    }

    public PsiIdentifier findDaoIdentifierNearFocusedElement() {
        PsiElement psiElement = getFocusedElement();
        if (psiElement instanceof PsiIdentifier || psiElement == null) {
            return (PsiIdentifier) psiElement;
        }
        PsiElement element = psiElement.getPrevSibling();
        if (!(element instanceof PsiExpressionStatement)) {
            return null;
        }
        PsiExpressionStatement expressionStatement = (PsiExpressionStatement) psiElement.getPrevSibling();
        return PsiTreeUtil.findChildOfType(expressionStatement, PsiIdentifier.class);
    }

    public PsiField findDaoField() {
        PsiIdentifier identifier = findDaoIdentifierNearFocusedElement();
        if (identifier == null) {
            return null;
        }
        return psiClass.findFieldByName(identifier.getText(), false);
    }

    public PsiClass findEntityClass() {

        PsiField psiField = findDaoField();

        if (psiField == null || !(psiField.getType() instanceof PsiClassReferenceType)) {
            return null;
        }
        PsiClassReferenceType type = (PsiClassReferenceType)psiField.getType();
        PsiClass psiClass = type.resolve();
        if (psiClass == null || !psiClass.isInterface()) {
            return null;
        }
        PsiClassType[] psiClassTypes = psiClass.getExtendsListTypes();
        if (psiClassTypes.length != 1 || !"BaseMapper".equals(psiClassTypes[0].getName())) {
            return null;
        }
        if (psiClassTypes[0].getParameters().length != 1) {
            return null;
        }
        PsiType psiType = psiClassTypes[0].getParameters()[0];
        if (!(psiType instanceof PsiClassReferenceType)) {
            return null;
        }
        PsiClassReferenceType entityType = (PsiClassReferenceType)psiType;
        return entityType.resolve();
    }

    public static boolean isEventOccurredInRepositoryFile(AnActionEvent actionEvent) {
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof PsiJavaFile)) {
            return false;
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        return (psiJavaFile.getName().endsWith("Repository.java") ||
                psiJavaFile.getName().endsWith("RepositoryImpl.java")) &&
                psiJavaFile.getClasses().length == 1 &&
                psiJavaFile.getContainingDirectory().getVirtualFile().getPath().contains("src/main/java") &&
                psiJavaFile.getPackageName().endsWith(".repository");
    }

    public static RepositoryStructureParser from(AnActionEvent actionEvent) {
        if (!isEventOccurredInRepositoryFile(actionEvent)) {
            throw new IllegalArgumentException("Not a repository file.");
        }
        return new RepositoryStructureParser(actionEvent);
    }
}

