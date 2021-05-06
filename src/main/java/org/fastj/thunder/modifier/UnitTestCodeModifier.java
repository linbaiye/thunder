package org.fastj.thunder.modifier;


import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.ProjectScope;
import org.assertj.core.util.Preconditions;
import org.fastj.thunder.logging.Logger;
import org.fastj.thunder.logging.LoggerFactory;
import org.fastj.thunder.until.NamingUtil;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Injects mockito contents.
 */
public class UnitTestCodeModifier implements CodeModifier {

    private final PsiJavaFile psiJavaFile;

    private final PsiClass unitTestClass;

    private final static String EXPECTED_CLASS_EXT = "UT";

    private final Project project;

    private final static String ACTION_GROUP_ID = "UnitTest";


    private final static Logger LOGGER = LoggerFactory.getLogger(UnitTestCodeModifier.class);

    private final static Set<String> SPRING_STEREOTYPES = ImmutableSet.of(
            "org.springframework.stereotype.Component",
            "org.springframework.stereotype.Controller",
            "org.springframework.stereotype.Repository",
            "org.springframework.stereotype.Service"
            );

    private final static String RUN_WITH_ANNOTATION = "org.junit.runner.RunWith(org.mockito.junit.MockitoJUnitRunner.class)";

    private UnitTestCodeModifier(PsiJavaFile psiJavaFile) {
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


    private void addField(PsiField psiField) {
        PsiField[] fields = unitTestClass.getAllFields();
        PsiElement addedElement;
        if (fields.length > 0) {
            addedElement = unitTestClass.addAfter(psiField, fields[fields.length - 1]);
        } else {
            addedElement = unitTestClass.add(psiField);
        }
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(addedElement);
        PsiParserFacade parserFacade = PsiParserFacade.SERVICE.getInstance(project);
        PsiElement lineBreak = parserFacade.createWhiteSpaceFromText("\n\n");
        addedElement.getParent().addBefore(lineBreak, addedElement);
    }


    private void tryAddTestedClass(PsiClass classUnderTest) {
        for (PsiField field : unitTestClass.getFields()) {
            if (field.getType() instanceof PsiClassReferenceType) {
                PsiClassReferenceType classReferenceType = (PsiClassReferenceType) field.getType();
                if (classReferenceType.getClassName().equals(classUnderTest.getName())) {
                    return;
                }
            }
        }
        PsiField newField = PsiElementFactory.getInstance(project).createFieldFromText("private " + classUnderTest.getName() +
                        " " + NamingUtil.nameClass(classUnderTest.getName()) + ";", unitTestClass);
        WriteCommandAction.runWriteCommandAction(project, "injectFields", ACTION_GROUP_ID, () -> {
            newField.getModifierList().addAnnotation("org.mockito.InjectMocks");
            addField(newField);
        });
    }


    /**
     * Mocks injected beans in the unit test class from the under class.
     * @param classUnderTest the class under test.
     */
    private void tryAddMockedFields(PsiClass classUnderTest) {
        List<PsiField> notMockedFields = Stream.of(classUnderTest.getAllFields())
                .filter(e -> !e.hasInitializer() &&
                        unitTestClass.findFieldByName(e.getName(), true) == null)
                .collect(Collectors.toList());
        for (PsiField notMockedField : notMockedFields) {
            PsiField copiedField = (PsiField)notMockedField.copy();
            PsiModifierList modifierList = copiedField.getModifierList();
            if (modifierList == null) {
                continue;
            }
            for (PsiElement modifier : modifierList.getChildren()) {
                if ("final".equals(modifier.getText())) {
                    modifier.delete();
                    break;
                }
            }
            WriteCommandAction.runWriteCommandAction(project, "injectFields", ACTION_GROUP_ID, () -> {
                modifierList.addAnnotation("org.mockito.Mock");
                addField(copiedField);
            });
        }
    }


    @Override
    public void tryModify() {
        PsiClass classUnderTest = getClassUnderTest();
        if (classUnderTest == null || !isAnnotatedWithSpringStereotype(classUnderTest)) {
            return;
        }
        tryAddRunnerWithAnnotation();
        tryAddTestedClass(classUnderTest);
        tryAddMockedFields(classUnderTest);
        LOGGER.info("Modified unit test class.");
    }

    private PsiClass getClassUnderTest() {
        return JavaPsiFacade.getInstance(project).findClass(getQualifiedClassNameTested(), ProjectScope.getProjectScope(project));
    }

    private boolean isAnnotatedWithSpringStereotype(PsiClass classUnderTest) {
        PsiAnnotation[] annotations = classUnderTest.getAnnotations();
        if (annotations.length < 1) {
            return false;
        }
        return Stream.of(annotations).anyMatch(e -> SPRING_STEREOTYPES.contains(e.getQualifiedName()));
    }


    public static Optional<UnitTestCodeModifier> create(PsiJavaFile psiJavaFile) {
        PsiClass[] psiClasses = psiJavaFile.getClasses();
        if (psiClasses.length != 1) {
            return Optional.empty();
        }
        if (psiClasses[0].getQualifiedName() == null || !psiClasses[0].getQualifiedName().endsWith(EXPECTED_CLASS_EXT)) {
            return Optional.empty();
        }
        return Optional.of(new UnitTestCodeModifier(psiJavaFile));
    }

}
