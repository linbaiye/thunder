package org.fastj.thunder.completer.validation;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.ContextType;
import org.fastj.thunder.context.TestThunderEvent;
import org.junit.Assert;

import java.util.Optional;

public class ValidationAnnotationCodeCompleterTest extends LightJavaCodeInsightFixtureTestCase {

    public void testAddAnnotations() {
        myFixture.addFileToProject("javax/validation/constraints/NotNull.java", "package javax.validation.constraints;" +
                "\n" +
                "@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })\n" +
                "@Retention(RUNTIME)\n" +
                "public @interface NotNull {\n" +
                "    String message() default \"\";\n" +
                "}");
        PsiFile file = myFixture.configureByText("Request.java", "package thunder.request;" +
                "public class Request {" +
                "private String firstName;" +
                "private String lastName;" +
                "}"
        );
        Optional<? extends CodeCompleter> optionalCodeCompleter = CodeCompleterFactory.getInstance().create(new TestThunderEvent(myFixture), ContextType.VALIDATOR_ANNOTATIONS);
        optionalCodeCompleter.ifPresent(
                e -> e.tryComplete()
        );
        PsiJavaFile javaFile = (PsiJavaFile) file;
        PsiField[] fields = javaFile.getClasses()[0].getAllFields();
        for (PsiField field : fields) {
            Assert.assertNotNull(field.getAnnotation("javax.validation.constraints.NotNull"));
        }
        optionalCodeCompleter.ifPresent(e -> e.tryComplete());
        fields = javaFile.getClasses()[0].getAllFields();
        for (PsiField field : fields) {
            Assert.assertEquals(1, field.getAnnotations().length);
        }
    }
}
