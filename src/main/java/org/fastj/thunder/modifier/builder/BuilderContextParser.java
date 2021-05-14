package org.fastj.thunder.modifier.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuilderContextParser {

    private final AnActionEvent anActionEvent;

    private final Caret caret;

    private final PsiJavaFile psiJavaFile;

    private final PsiReferenceExpression referenceExpression;

    private PsiClass builder;

    private PsiType builderType;

    public BuilderContextParser(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
        caret = anActionEvent.getData(CommonDataKeys.CARET);
        assert caret != null;
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        psiJavaFile = (psiFile instanceof PsiJavaFile) ? (PsiJavaFile)  psiFile : null;
        assert psiJavaFile != null;
        PsiElement pe = psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
        assert pe != null;
        PsiElement psiElement = pe.getParent();
        referenceExpression = (PsiReferenceExpression) psiElement;
        parseBuilderClass(referenceExpression);
    }

    private void parseBuilderClass(PsiReferenceExpression referenceExpression) {
        referenceExpression.acceptChildren(new JavaElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                PsiMethod psiMethod = expression.resolveMethod();
                if (psiMethod == null) {
                    return;
                }
                builderType = psiMethod.getReturnType();
                builder = PsiTypesUtil.getPsiClass(builderType);
            }
        });
    }

    public PsiClass getBuilderClass() {
        return builder;
    }

    /**
     * Gets the class the builder builds.
     * @return the result class.
     */
    public PsiClass parseResultClass() {
        if (builder == null) {
            return null;
        }
        PsiMethod[] methods = builder.findMethodsByName("build", false);
        if (methods.length != 1) {
            return null;
        }
        PsiType psiType = methods[0].getReturnType();
        return PsiTypesUtil.getPsiClass(psiType);
    }

    public List<PsiMethod> parseChainMethods() {
        if (builder == null) {
            return Collections.emptyList();
        }
        PsiMethod[] methods = builder.getMethods();
        return Stream.of(methods).filter(e -> builderType.equals(e.getReturnType())).collect(Collectors.toList());
    }


    private PsiMethod findMethod(PsiElement element) {
        PsiMethod method = (element instanceof PsiMethod) ? (PsiMethod) element :
                PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method != null && method.getContainingClass() instanceof PsiAnonymousClass) {
            return findMethod(method.getParent());
        }
        return method;
    }

    public Map<String, PsiType> parseBuilderSourceParameterCandidates() {
        PsiMethod method = findMethod(referenceExpression);
        if (method == null) {
            return Collections.emptyMap();
        }
        PsiParameter[] parameters = PsiTreeUtil.getChildrenOfType(method, PsiParameter.class);
        if (parameters == null) {
            return Collections.emptyMap();
        }
        Map<String, PsiType> result = new HashMap<>();
        for (PsiParameter parameter : parameters) {
            if (parameter.getType().getCanonicalText().startsWith("java")) {
                continue;
            }
            result.putIfAbsent(parameter.getName(), parameter.getType());
        }
        PsiLocalVariable[] variables = PsiTreeUtil.getChildrenOfType(method, PsiLocalVariable.class);
        if (variables != null) {
            for (PsiLocalVariable variable : variables) {
                if (variable.getType().getCanonicalText().startsWith("java")) {
                    continue;
                }
                result.putIfAbsent(variable.getName(), variable.getType());
            }
        }
        return result;
    }
}
