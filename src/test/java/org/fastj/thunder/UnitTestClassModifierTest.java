package org.fastj.thunder;

import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.modifier.UnitTestClassModifier;
import org.intellij.lang.annotations.Language;

import java.util.Optional;


public class UnitTestClassModifierTest extends LightJavaCodeInsightFixtureTestCase {

    @Language("JAVA")
    private final static String TEST_FILE_CONTENT = "" +
            "package org.fastj.thunder;\n" +
            "import lombok.AllArgsConstructor;\n" +
            "import lombok.extern.slf4j.Slf4j;\n" +
            "import org.springframework.stereotype.Service;\n" +
            "@Slf4j\n" +
            "@AllArgsConstructor\n" +
            "@Service(\"testingService\")\n" +
            "public class TestingService {\n" +
            "    private final ServiceOne serviceOne;\n" +
            "    private final ServiceTwo serviceTwo;\n" +
            "}";

    @Language("JAVA")
    private final static String UNIT_TEST_CONTENT =
            "package org.fastj.thunder;\n" +
            "public class TestingServiceUT {\n" +
            "}";


    @Language("JAVA")
    private final static String SPRING_ANNOTATION =
            "package org.springframework.stereotype;\n" +
                    "@Target(ElementType.TYPE)\n" +
                    "@Retention(RetentionPolicy.RUNTIME)\n" +
                    "public @interface Service {" +
                    "String value() default \"\";\n" +
                    "}";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.addFileToProject("src/main/java/org/fastj/thunder/TestingService.java", TEST_FILE_CONTENT);
        myFixture.addFileToProject("src/main/java/org/springframework/stereotype/Service.java", SPRING_ANNOTATION);
        myFixture.addFileToProject("src/main/test/org/fastj/thunder/TestingServiceUT.java", UNIT_TEST_CONTENT);
    }


    public void testModifyUnitTestClass() throws Exception {
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(getProject(), "TestingServiceUT.java",
                ProjectScope.getProjectScope(getProject()));
        Optional<UnitTestClassModifier> optional = UnitTestClassModifier.create((PsiJavaFile)psiFiles[0]);
        UnitTestClassModifier modifier = optional.orElseThrow(IllegalArgumentException::new);
        modifier.tryModify();
        PsiClass modifiedClass = JavaPsiFacade.getInstance(getProject()).findClass("org.fastj.thunder.TestingServiceUT",
                ProjectScope.getProjectScope(getProject()));
        PsiAnnotation[] annotations = modifiedClass.getAnnotations();
        assertSame(1, annotations.length);
        optional = UnitTestClassModifier.create((PsiJavaFile)psiFiles[0]);
        modifier = optional.orElseThrow(IllegalArgumentException::new);
        modifier.tryModify();
        modifiedClass = JavaPsiFacade.getInstance(getProject()).findClass("org.fastj.thunder.TestingServiceUT",
                ProjectScope.getProjectScope(getProject()));
        annotations = modifiedClass.getAnnotations();
        assertSame(1, annotations.length);
    }
}
