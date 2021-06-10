package org.fastj.thunder.completer.injectmocks;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.until.NamingUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InjectMocksCodeCompleter implements CodeCompleter {

    private final InjectMocksContextAnalyser contextAnalyser;

    private final static String RUN_WITH_ANNOTATION = "org.junit.runner.RunWith(org.mockito.junit.MockitoJUnitRunner.class)";

    public InjectMocksCodeCompleter(InjectMocksContextAnalyser contextAnalyser) {
        this.contextAnalyser = contextAnalyser;
    }

    private boolean hasRunWithAnnotation() {
        PsiClass psiClass = contextAnalyser.getUnitTestClass();
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (annotation.getText().contains("@RunWith(")) {
                return true;
            }
        }
        return false;
    }


    private Optional<PsiField> createTestedClassFieldIfAbsent() {
        PsiField[] fields = contextAnalyser.getFieldsOfUnitTestClass();
        PsiClass psiClass = contextAnalyser.getTestedClass();
        for (PsiField field : fields) {
            if (field.getType() instanceof PsiClassReferenceType) {
                PsiClassReferenceType classReferenceType = (PsiClassReferenceType) field.getType();
                if (classReferenceType.getClassName().equals(psiClass.getName())) {
                    return Optional.empty();
                }
            }
        }
        return Optional.of(
                PsiElementFactory.getInstance(contextAnalyser.getProject()).createFieldFromText("private " + psiClass.getName() +
                " " + NamingUtil.nameClass(psiClass.getName()) + ";", contextAnalyser.getUnitTestClass())
        );
    }


    private void addFieldToUnitTestClass(PsiField psiField) {
        PsiClass unitTestClass = contextAnalyser.getUnitTestClass();
        PsiField[] fields = unitTestClass.getAllFields();
        PsiElement addedElement;
        if (fields.length > 0) {
            addedElement = unitTestClass.addAfter(psiField, fields[fields.length - 1]);
        } else {
            addedElement = unitTestClass.add(psiField);
        }
        JavaCodeStyleManager.getInstance(contextAnalyser.getProject()).shortenClassReferences(addedElement);
        PsiParserFacade parserFacade = PsiParserFacade.SERVICE.getInstance(contextAnalyser.getProject());
        PsiElement lineBreak = parserFacade.createWhiteSpaceFromText("\n\n");
        addedElement.getParent().addBefore(lineBreak, addedElement);
    }


    private List<PsiField> createUnmockedFields() {
        PsiClass unitTestClass = contextAnalyser.getUnitTestClass();
        List<PsiField> notMockedFields = Stream.of(contextAnalyser.getTestedClass().getAllFields())
                .filter(e -> !e.hasInitializer() &&
                        unitTestClass.findFieldByName(e.getName(), true) == null)
                .collect(Collectors.toList());
        return notMockedFields.stream().map(e -> {
            PsiField copiedField = (PsiField)e.copy();
            if (copiedField == null) {
                return null;
            }
            PsiModifierList modifierList = copiedField.getModifierList();
            if (modifierList == null) {
                return null;
            }
            for (PsiElement modifier : modifierList.getChildren()) {
                if ("final".equals(modifier.getText())) {
                    modifier.delete();
                    break;
                }
            }
            return copiedField;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    @Override
    public void tryComplete() {
        final boolean needRunWithAnnotation = !hasRunWithAnnotation();
        final Optional<PsiField> optionalPsiField = createTestedClassFieldIfAbsent();
        List<PsiField> unmockedFields = createUnmockedFields();
        WriteCommandAction.runWriteCommandAction(contextAnalyser.getProject(), () -> {
            if (needRunWithAnnotation) {
                PsiModifierList modifierList = contextAnalyser.getUnitTestClass().getModifierList();
                if (modifierList != null) {
                    PsiAnnotation annotation = modifierList.addAnnotation(RUN_WITH_ANNOTATION);
                    JavaCodeStyleManager.getInstance(contextAnalyser.getProject()).shortenClassReferences(annotation);
                }
            }
            optionalPsiField.ifPresent( e -> {
                if (e.getModifierList() != null) {
                    e.getModifierList().addAnnotation("org.mockito.InjectMocks");
                    addFieldToUnitTestClass(e);
                }
            });
            for (PsiField unmockedField : unmockedFields) {
                if (unmockedField.getModifierList() != null) {
                    unmockedField.getModifierList().addAnnotation("org.mockito.Mock");
                    addFieldToUnitTestClass(unmockedField);
                }
            }
        });
    }
}
