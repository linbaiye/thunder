package org.fastj.thunder.modifier;


import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.ProjectScope;
import org.assertj.core.util.Preconditions;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Injects mockito contents.
 */
public class UnitTestClassModifier implements ClassModifier {

    private final PsiJavaFile psiJavaFile;

    private final PsiClass unitTestClass;

    private final static String EXPECTED_CLASS_EXT = "UT";

    private final Project project;

    private final static String ACTION_GROUP_ID = "UnitTest";

    private final static Set<String> SPRING_STEREOTYPES = ImmutableSet.of(
            "org.springframework.stereotype.Component",
            "org.springframework.stereotype.Controller",
            "org.springframework.stereotype.Repository",
            "org.springframework.stereotype.Service"
            );

    private final static String RUN_WITH_ANNOTATION = "org.junit.runner.RunWith(org.mockito.junit.MockitoJUnitRunner.class)";

    private UnitTestClassModifier(PsiJavaFile psiJavaFile) {
        Preconditions.checkNotNull(psiJavaFile);
        this.psiJavaFile = psiJavaFile;
        this.unitTestClass = this.psiJavaFile.getClasses()[0];
        this.project = psiJavaFile.getProject();
    }

    private String getQualifiedClassNameTested() {
        return unitTestClass.getQualifiedName().replaceAll("^(.*)UT$", "$1");
    }

    private void tryAddRunnerWithAnnotation() {
        PsiAnnotation[] annotations = unitTestClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (annotation.getText().contains("RunWith(")) {
                return;
            }
        }
        WriteCommandAction.runWriteCommandAction(project, "modifyRunner", ACTION_GROUP_ID, () -> {
            if (unitTestClass.getModifierList() != null) {
                PsiAnnotation annotation = unitTestClass.getModifierList().addAnnotation(RUN_WITH_ANNOTATION);
                JavaCodeStyleManager.getInstance(project).shortenClassReferences(annotation);
            }
        });
    }

    private void tryAddFields(PsiClass classUnderTest) {
        Map<String, PsiField> possibleInjections = Stream.of(classUnderTest.getAllFields())
                .filter(e -> !e.hasInitializer())
                .collect(Collectors.toMap(PsiField::getName, Function.identity()));
        for (String s : possibleInjections.keySet()) {
            
        }
    }


    @Override
    public void tryModify() {
        PsiClass classUnderTest = getClassUnderTest();
        if (classUnderTest == null || !isAnnotatedWithSpringStereotype(classUnderTest)) {
            return;
        }
        tryAddRunnerWithAnnotation();
    }

    private PsiClass getClassUnderTest() {
        return JavaPsiFacade.getInstance(psiJavaFile.getProject()).findClass(getQualifiedClassNameTested(), ProjectScope.getProjectScope(project));
    }

    private boolean isAnnotatedWithSpringStereotype(PsiClass classUnderTest) {
        PsiAnnotation[] annotations = classUnderTest.getAnnotations();
        if (annotations.length < 1) {
            return false;
        }
        return Stream.of(annotations).anyMatch(e -> SPRING_STEREOTYPES.contains(e.getQualifiedName()));
    }


    public static Optional<UnitTestClassModifier> create(PsiJavaFile psiJavaFile) {
        PsiClass[] psiClasses = psiJavaFile.getClasses();
        if (psiClasses.length != 1) {
            return Optional.empty();
        }
        if (psiClasses[0].getQualifiedName() == null || !psiClasses[0].getQualifiedName().endsWith(EXPECTED_CLASS_EXT)) {
            return Optional.empty();
        }
        return Optional.of(new UnitTestClassModifier(psiJavaFile));
    }

}
