package org.fastj.thunder.modifier.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuilderContextParser {

    private final AnActionEvent anActionEvent;

    private final Caret caret;

    private final PsiJavaFile psiJavaFile;

    private final PsiElement elementAtCaret;

    private PsiClass builder;

    private PsiType builderType;

    public BuilderContextParser(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
        caret = anActionEvent.getData(CommonDataKeys.CARET);
        assert caret != null;
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        psiJavaFile = (psiFile instanceof PsiJavaFile) ? (PsiJavaFile)  psiFile : null;
        assert psiJavaFile != null;
        elementAtCaret = psiJavaFile.findElementAt(caret.getCaretModel().getOffset());
        assert elementAtCaret != null;
        findBuilder(elementAtCaret);
    }

    private void findBuilder(PsiElement psiElement) {
        PsiElement statement = psiElement;
        if (psiElement instanceof PsiWhiteSpace) {
            statement = psiElement.getPrevSibling();
            if (statement instanceof PsiDeclarationStatement) {
                statement = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression.class);
            }
        } else if (psiElement.getParent() instanceof PsiReferenceExpression) {
            statement = psiElement.getParent();
        }
        if (statement == null) {
            return;
        }
        if (statement instanceof PsiMethodCallExpression) {
            PsiMethod psiMethod = ((PsiMethodCallExpression)statement).resolveMethod();
            if (psiMethod == null) {
                return;
            }
            builderType = psiMethod.getReturnType();
            builder = PsiTypesUtil.getPsiClass(builderType);
            return;
        }
        statement.acceptChildren(new JavaElementVisitor() {
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

    public PsiElement getElementAtCaret() {
        return elementAtCaret;
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


    public Editor getEditor() {
        return anActionEvent.getData(CommonDataKeys.EDITOR);
    }


    public Project getProject() {
        return anActionEvent.getProject();
    }


    private PsiMethod findMethod(PsiElement element) {
        PsiMethod method = (element instanceof PsiMethod) ? (PsiMethod) element :
                PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method != null && method.getContainingClass() instanceof PsiAnonymousClass) {
            return findMethod(method.getParent());
        }
        return method;
    }


    private boolean isJreType(PsiType type) {
        return type.getCanonicalText().startsWith("java") ||
                type instanceof PsiPrimitiveType ;
    }

    public Map<String, PsiType> parseBuilderSourceParameterCandidates() {
        PsiMethod method = findMethod(elementAtCaret);
        if (method == null) {
            return Collections.emptyMap();
        }
        int expressionOffset = elementAtCaret.getTextOffset();
        Map<String, PsiType> result = new HashMap<>();
        Collection<PsiParameter> parameters = PsiTreeUtil.findChildrenOfType(method, PsiParameter.class);
        for (PsiParameter parameter : parameters) {
            if (isJreType(parameter.getType()) || parameter.getTextOffset() > expressionOffset) {
                continue;
            }
            result.putIfAbsent(parameter.getName(), parameter.getType());
        }
        Collection<PsiLocalVariable> variables = PsiTreeUtil.findChildrenOfType(method, PsiLocalVariable.class);
        for (PsiLocalVariable variable : variables) {
            if (isJreType(variable.getType()) || variable.getTextOffset() > expressionOffset) {
                continue;
            }
            result.putIfAbsent(variable.getName(), variable.getType());
        }
        return result;
    }
}
