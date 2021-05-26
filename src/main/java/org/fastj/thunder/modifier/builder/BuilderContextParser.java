package org.fastj.thunder.modifier.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.fastj.thunder.modifier.AbstractContextParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuilderContextParser extends AbstractContextParser {

    private PsiClass builder;

    private PsiType builderType;

    // XXX.builder()
    private PsiMethod buildingMethod;

    private PsiClass resultClass;

    private PsiMethodCallExpression methodCallExpression;

    private Map<String, PsiType> sourceParameterCandidates;

    /**
     * The variable to which the built result will be assigned.
     */
    private PsiVariable selfVariable;

    public BuilderContextParser(AnActionEvent anActionEvent) {
        super(anActionEvent);
        parseBuilderMethod();
        parseResultClass();
        parseBuilderSourceParameterCandidates();
    }

    private void visitMethodCalling(PsiElement psiElement) {
        psiElement.acceptChildren(new JavaElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                methodCallExpression = expression;
                buildingMethod = expression.resolveMethod();
            }
        });
    }

    private void parseResultClass() {
        if (builder != null) {
            PsiMethod[] methods = builder.findMethodsByName("build", false);
            if (methods.length == 1) {
                PsiType psiType = methods[0].getReturnType();
                resultClass = PsiTypesUtil.getPsiClass(psiType);
            }
        }
    }

    private void parseWhenWhitespace(PsiWhiteSpace whiteSpace) {
        PsiElement psiElement = whiteSpace.getPrevSibling();
        if (psiElement instanceof PsiExpressionStatement) {
            visitMethodCalling(psiElement);
        } else if (psiElement instanceof PsiDeclarationStatement) {
            psiElement.acceptChildren(new JavaElementVisitor() {
                @Override
                public void visitLocalVariable(PsiLocalVariable variable) {
                    selfVariable = variable;
                    visitMethodCalling(variable);
                }
            });
        }
    }

    private void parseWhenIdentifier(PsiIdentifier psiIdentifier) {
        PsiMethodCallExpression methodCallExpression =
                PsiTreeUtil.getParentOfType(psiIdentifier, PsiMethodCallExpression.class);
        if (methodCallExpression != null) {
            buildingMethod = methodCallExpression.resolveMethod();
        }
    }

    private void parseBuilderMethod() {
        if (elementAtCaret instanceof PsiWhiteSpace) {
            parseWhenWhitespace((PsiWhiteSpace)elementAtCaret);
        } else if (elementAtCaret instanceof PsiIdentifier) {
            parseWhenIdentifier((PsiIdentifier) elementAtCaret);
        }
        if (buildingMethod != null) {
            builderType = buildingMethod.getReturnType();
            builder = PsiTypesUtil.getPsiClass(builderType);
        }
    }

    public PsiClass getBuilderClass() {
        return builder;
    }

    /**
     * Gets the class the builder builds.
     * @return the result class.
     */
    public PsiClass getResultClass() {
        return resultClass;
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

    public Map<String, PsiType> getSourceParameterCandidates() {
        return sourceParameterCandidates;
    }

    private void addToCandidates(Collection<? extends PsiVariable> parameters) {
        if (parameters == null) {
            return;
        }
        int expressionOffset = elementAtCaret.getTextOffset();
        for (PsiVariable parameter : parameters) {
            if (parameter == selfVariable || parameter.getTextOffset() > expressionOffset) {
                continue;
            }
            sourceParameterCandidates.putIfAbsent(parameter.getName(), parameter.getType());
        }
    }

    public PsiMethodCallExpression getMethodCallExpression() {
        return methodCallExpression;
    }

    public void parseBuilderSourceParameterCandidates() {
        PsiMethod method = findMethod(elementAtCaret);
        if (method == null) {
            sourceParameterCandidates = Collections.emptyMap();
            return;
        }
        sourceParameterCandidates = new HashMap<>();
        Collection<PsiParameter> parameters = PsiTreeUtil.findChildrenOfType(method, PsiParameter.class);
        addToCandidates(parameters);
        Collection<PsiLocalVariable> variables = PsiTreeUtil.findChildrenOfType(method, PsiLocalVariable.class);
        addToCandidates(variables);
    }
}
