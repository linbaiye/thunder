package org.fastj.thunder;

import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.modifier.UnitTestCodeModifier;
import org.intellij.lang.annotations.Language;

import java.util.Optional;


public class UnitTestCodeModifierTest extends LightJavaCodeInsightFixtureTestCase {

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

    @Language("JAVA")
    private final static String RUN_WITH_CLASS=
            "package org.junit.runner;\n" +
            "@Target({FIELD, PARAMETER})\n" +
            "@Retention(RUNTIME)\n" +
            "public @interface RunWith {    Class<? extends Runner> value(); }";

    @Language("JAVA")
    private final static String MOCKITO_RUNNER_CLASS =
            "package org.mockito.junit;" +
                    "public class MockitoJUnitRunner { }";



    @Language("JAVA")
    private final static String MOCK_CLASS =
            "package org.mockito;\n" +
                    "@Target({FIELD, PARAMETER})\n" +
                    "@Retention(RUNTIME)\n" +
                    "public @interface Mock { }";


    @Language("JAVA")
    private final static String INJECT_MOCK_CLASS =
            "package org.mockito;\n" +
                    "@Target({FIELD, PARAMETER})\n" +
                    "@Retention(RUNTIME)\n" +
                    "public @interface InjectMocks { }";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.addFileToProject("main/java/org/fastj/thunder/TestingService.java", TEST_FILE_CONTENT);
        myFixture.addFileToProject("main/java/org/springframework/stereotype/Service.java", SPRING_ANNOTATION);
        myFixture.addFileToProject("test/java/org/fastj/thunder/TestingServiceUT.java", UNIT_TEST_CONTENT);
        myFixture.addFileToProject("main/java/org/mockito/Mock.java", MOCK_CLASS);
        myFixture.addFileToProject("main/java/org/mockito/InjectMocks.java", INJECT_MOCK_CLASS);
        myFixture.addFileToProject("main/java/org/mockito/junit/MockitoJUnitRunner.java", MOCKITO_RUNNER_CLASS);
        myFixture.addFileToProject("main/java/org/junit/runner/RunWith.java", RUN_WITH_CLASS);
    }


    public void testModifyUnitTestClass() throws Exception {
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(getProject(), "TestingServiceUT.java",
                ProjectScope.getProjectScope(getProject()));
        Optional<UnitTestCodeModifier> optional = UnitTestCodeModifier.create((PsiJavaFile)psiFiles[0]);
        UnitTestCodeModifier modifier = optional.orElseThrow(IllegalArgumentException::new);
        modifier.tryModify();
        PsiClass modifiedClass = JavaPsiFacade.getInstance(getProject()).findClass("org.fastj.thunder.TestingServiceUT",
                ProjectScope.getProjectScope(getProject()));
        System.out.println(modifiedClass.getText());
    }
}
