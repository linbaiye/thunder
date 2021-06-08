package org.fastj.thunder.completer.validation;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.ContextType;
import org.fastj.thunder.context.TestThunderEvent;
import org.junit.Assert;

import java.util.Optional;

public class ValidationAnnotationCodeCompleterTest extends LightJavaCodeInsightFixtureTestCase {

    private void addAnnotationClass() {
        myFixture.addFileToProject("javax/validation/constraints/NotNull.java", "package javax.validation.constraints;" +
                "\n" +
                "@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })\n" +
                "@Retention(RUNTIME)\n" +
                "public @interface NotNull {\n" +
                "    String message() default \"\";\n" +
                "}");
    }

    public void testAddAnnotations() {
        addAnnotationClass();
        PsiFile file = myFixture.configureByText("Request.java", "package thunder.request;" +
                "public class Request {" +
                "private final String noAnnotation<caret>;" +
                "private static String noAnnotation1;" +
                "private String firstName;" +
                "private String lastName;" +
                "}"
        );
        Optional<? extends CodeCompleter> optionalCodeCompleter = CodeCompleterFactory.getInstance().create(new TestThunderEvent(myFixture), ContextType.VALIDATOR_ANNOTATIONS);
        optionalCodeCompleter.ifPresent(
                CodeCompleter::tryComplete
        );
        PsiJavaFile javaFile = (PsiJavaFile) file;
        PsiField[] fields = javaFile.getClasses()[0].getAllFields();
        for (PsiField field : fields) {
            if (field.getName().equalsIgnoreCase("noAnnotation") ||
            field.getName().equalsIgnoreCase("noAnnotation1"))  {
                Assert.assertNull(field.getAnnotation("javax.validation.constraints.NotNull"));
            } else {
                Assert.assertNotNull(field.getAnnotation("javax.validation.constraints.NotNull"));
            }
        }
        optionalCodeCompleter.ifPresent(CodeCompleter::tryComplete);
        fields = javaFile.getClasses()[0].getAllFields();
        for (PsiField field : fields) {
            if (!field.getName().equalsIgnoreCase("noAnnotation") &&
                    !field.getName().equalsIgnoreCase("noAnnotation1")) {
                Assert.assertEquals(1, field.getAnnotations().length);
            }
        }
    }

    public void testAddAnnotationsToInnerClass() {
        PsiFile file = myFixture.configureByText("Request.java", "package thunder.request;" +
                "public class Request {" +
                "public static class InnerRequest {" +
                "private String needAnnotation<caret>;" +
                "}" +
                "}"
        );
        Optional<? extends CodeCompleter> optionalCodeCompleter = CodeCompleterFactory.getInstance().create(
                new TestThunderEvent(myFixture), ContextType.VALIDATOR_ANNOTATIONS);
        optionalCodeCompleter.ifPresent(
                CodeCompleter::tryComplete
        );
        PsiElement element = myFixture.getElementAtCaret();
        PsiField[] fields = PsiTreeUtil.getParentOfType(element, PsiClass.class).getAllFields();
        Assert.assertEquals(1, fields.length);
        for (PsiField field : fields) {
            Assert.assertNotNull(field.getAnnotation("javax.validation.constraints.NotNull"));
        }
    }
}
