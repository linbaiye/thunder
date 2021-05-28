package org.fastj.thunder.modifier.builder;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.fastj.thunder.modifier.AbstractScopeParser;
import org.fastj.thunder.scope.ThunderEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LombokBuilderScopeParser extends AbstractScopeParser {

    private PsiClass builder;

    private PsiType builderType;

    // XXX.builder()
    private PsiMethod buildingMethod;

    private PsiClass resultClass;

    private PsiMethodCallExpression methodCallExpression;

    /**
     * The variable to which the built object is assigned.
     */
    private PsiLocalVariable selfVariable;

    /**
     * Parameters, variables that can provide the builder
     * methods with candidates.
     */
    private Map<String, PsiType> sourceParameterCandidates;


    public LombokBuilderScopeParser(ThunderEvent thunderEvent) {
        super(thunderEvent);
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
        if (builder != null &&
                builder.getParent() instanceof PsiClass) {
            resultClass = (PsiClass) builder.getParent();
        }
    }

//    private void parseSibling(PsiElement prevSibling) {
//        if (prevSibling instanceof PsiDeclarationStatement) {
//            prevSibling.acceptChildren(new JavaElementVisitor() {
//                @Override
//                public void visitLocalVariable(PsiLocalVariable variable) {
//                    selfVariable = variable;
//                    visitMethodCalling(variable);
//                }
//            });
//        } else if (prevSibling instanceof PsiMethodCallExpression) {
//            methodCallExpression = (PsiMethodCallExpression) prevSibling;
//            buildingMethod = methodCallExpression.resolveMethod();
//        } else {
//            visitMethodCalling(prevSibling);
//        }
//    }

    private void parseWhenIdentifier(PsiIdentifier psiIdentifier) {
        PsiMethodCallExpression callExpression =
                PsiTreeUtil.getParentOfType(psiIdentifier, PsiMethodCallExpression.class);
        if (callExpression != null) {
            this.methodCallExpression = callExpression;
            buildingMethod = callExpression.resolveMethod();
        }
    }

    private void parseBuilderMethod() {
        PsiElement elementAtCaret = thunderEvent.getElementAtCaret();
        if (elementAtCaret instanceof PsiIdentifier) {
            parseWhenIdentifier((PsiIdentifier) elementAtCaret);
        } else {
            PsiElement sibling = elementAtCaret.getPrevSibling();
            PsiMethodCallExpression expression = PsiTreeUtil.findChildOfType(sibling, PsiMethodCallExpression.class);
            methodCallExpression = expression != null &&
                    expression.getTextOffset() <= thunderEvent.getCaretOffset() ?
                    expression : null;
            if (methodCallExpression != null) {
                PsiLocalVariable localVariable = PsiTreeUtil.findChildOfType(sibling, PsiLocalVariable.class);
                buildingMethod = expression.resolveMethod();
                if (PsiTreeUtil.isAncestor(localVariable, methodCallExpression, true)) {
                    this.selfVariable = localVariable;
                }
            }
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

    private Set<String> getBuilderFieldNames() {
        if (builder == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(builder.getFields()).map(PsiField::getName).collect(Collectors.toSet());
    }

    public List<PsiMethod> parseChainMethods() {
        if (builder == null) {
            return Collections.emptyList();
        }
        Set<String> fieldNames = getBuilderFieldNames();
        PsiMethod[] methods = builder.getMethods();
        return Stream.of(methods).filter(e -> fieldNames.contains(e.getName())).collect(Collectors.toList());
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
        int expressionOffset = thunderEvent.getElementAtCaret().getTextOffset();
        for (PsiVariable parameter : parameters) {
            if (selfVariable == parameter || parameter.getTextOffset() > expressionOffset) {
                continue;
            }
            sourceParameterCandidates.putIfAbsent(parameter.getName(), parameter.getType());
        }
    }

    public PsiMethodCallExpression getMethodCallExpression() {
        return methodCallExpression;
    }

    public boolean isBuilderMethodContainedInLambda() {
        return methodCallExpression != null &&
                PsiTreeUtil.getParentOfType(methodCallExpression, PsiLambdaExpression.class) != null;
    }

    public void parseBuilderSourceParameterCandidates() {
        PsiMethod method = findMethod(thunderEvent.getElementAtCaret());
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

    public Project getProject() {
        return thunderEvent.getProject();
    }

    public Editor getEditor() {
        return thunderEvent.getEditor();
    }

    public PsiElement getElementAtCaret() {
        return thunderEvent.getElementAtCaret();
    }
}
