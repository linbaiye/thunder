package org.fastj.thunder.modifier.persistence;


import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.fastj.thunder.logging.ConsoleViewPrinter;
import org.fastj.thunder.modifier.CodeModifier;
import org.fastj.thunder.until.NamingUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepositoryCodeModifier implements CodeModifier {

    private final PsiMethod psiMethod;

    private final PsiClass entityClass;

    private final PsiElement focusedElement;

    private final PsiField daoOrRepositoryField;

    private final Project project;

    private final static Pattern WORD_PATTERN = Pattern.compile("([A-Z][a-z]+)");

    private final static Set<String> AND_OR = ImmutableSet.of("And", "Or");

    private final static Map<String, String> OPERATORS_MAP = new HashMap<>();
    static {
        OPERATORS_MAP.put("LessThan", "lt");
        OPERATORS_MAP.put("Lt", "lt");
        OPERATORS_MAP.put("LessThanOrEqual", "le");
        OPERATORS_MAP.put("Le", "le");
        OPERATORS_MAP.put("In", "in");
        OPERATORS_MAP.put("Equal", "eq");
        OPERATORS_MAP.put("GreaterThan", "gt");
        OPERATORS_MAP.put("Gt", "gt");
        OPERATORS_MAP.put("GreaterThanOrEqual", "ge");
        OPERATORS_MAP.put("Ge", "ge");
    }

    public RepositoryCodeModifier(PsiMethod method,
                                  PsiClass entityClass,
                                  PsiElement focusedElement,
                                  PsiField daoOrRepositoryField) {
        this.psiMethod = method;
        this.entityClass = entityClass;
        this.focusedElement = focusedElement;
        this.daoOrRepositoryField = daoOrRepositoryField;
        this.project = focusedElement.getProject();
    }

    private List<Criterion> parseColumnNamesInMethodName() {
        String statement = psiMethod.getName().replaceFirst("findBy", "");
        Matcher matcher = WORD_PATTERN.matcher(statement);
        List<String> wordList = new LinkedList<>();
        while (matcher.find()) {
            wordList.add(matcher.group(1));
        }
        PsiParameterList parameterList = psiMethod.getParameterList();
        if ((parameterList.getParametersCount() < wordList.size())) {
            ConsoleViewPrinter.getInstance(project).appendInfo("Method " + psiMethod.getName() + " has less ");
        }
        List<Criterion> criterionList = new LinkedList<>();
        String currentProperty = null, currentOperator = null;
        int position = 0;
        for (String s : wordList) {
            if (AND_OR.contains(s)) {
                if (currentProperty != null) {
                    criterionList.add(new Criterion(currentProperty, currentOperator == null ? "Equal": currentOperator,  position++));
                }
                currentProperty = null;
                currentOperator = null;
            } else if (OPERATORS_MAP.containsKey(s)) {
                currentOperator = s;
            } else {
                currentProperty = currentProperty != null ? (currentProperty + s) : s;
            }
        }
        if (currentProperty != null) {
            criterionList.add(new Criterion(currentProperty, currentOperator == null ? "Equal": currentOperator, position));
        }
        return criterionList;
    }

    private boolean methodReturnsList() {
        PsiType psiType = psiMethod.getReturnType();
        return psiType != null && psiType.getCanonicalText().contains("List<");
    }


    private String buildLocalVariable() {
        StringBuilder stringBuilder = new StringBuilder();
        if (methodReturnsList()) {
            stringBuilder.append("List<");
            stringBuilder.append(entityClass.getName());
            stringBuilder.append("> ");
            stringBuilder.append(NamingUtil.nameClass(entityClass.getName()));
            stringBuilder.append("List");
        } else {
            stringBuilder.append(entityClass.getName());
            stringBuilder.append(" ");
            stringBuilder.append(NamingUtil.nameClass(entityClass.getName()));
        }
        return stringBuilder.toString();
    }


    @Override
    public void tryModify() {
        List<Criterion> criteria = parseColumnNamesInMethodName();
        if (criteria.isEmpty()) {
            return;
        }
        String localVariable = buildLocalVariable();
        StringBuilder stringBuilder = new StringBuilder(localVariable + " = ");
        stringBuilder.append(daoOrRepositoryField.getName());
        stringBuilder.append(".selectOne(new LambdaQueryWrapper<");
        stringBuilder.append(entityClass.getName());
        stringBuilder.append(">()");
        psiMethod.getParameterList().getParameters();
        for (Criterion criterion : criteria) {
            if (entityClass.findFieldByName(criterion.getPropertyName(), false) != null) {
                stringBuilder.append(criterion.buildQuery(entityClass.getName()));
            }
        }
        stringBuilder.append(");");
        PsiElement expression = PsiElementFactory.getInstance(project).createStatementFromText(stringBuilder.toString(), psiMethod);
        WriteCommandAction.runWriteCommandAction(project, "mybatis", "Repository", () -> {
            focusedElement.replace(expression);
        });
    }

    private class Criterion {

        private final String property;

        private final String operator;

        private final int position;

        public Criterion(String property,
                         String operator, int position) {
            this.property = property;
            this.operator = operator;
            this.position = position;
        }

        public String getPropertyName() {
            return NamingUtil.nameProperty(property);
        }

        public String buildQuery(String entityName) {
            String paramName = null;
            PsiParameterList parameterList = psiMethod.getParameterList();
            if (parameterList.getParametersCount() >= position + 1) {
                paramName = parameterList.getParameters()[position].getName();
            }
            return String.format(".%s(%s::get%s, %s)", OPERATORS_MAP.get(operator), entityName,
                    property, paramName != null ? paramName : NamingUtil.nameProperty(property));
        }
    }

    public static Optional<RepositoryCodeModifier> from(PsiMethod method,
                                  PsiClass entityClass,
                                  PsiElement focusedElement,
                                  PsiField daoOrRepositoryField) {
        if (method == null || entityClass == null || focusedElement == null || daoOrRepositoryField == null) {
            return Optional.empty();
        } else {
            return Optional.of(new RepositoryCodeModifier(method, entityClass, focusedElement, daoOrRepositoryField));
        }
    }
}
