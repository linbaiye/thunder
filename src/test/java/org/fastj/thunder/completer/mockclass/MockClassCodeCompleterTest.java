package org.fastj.thunder.completer.mockclass;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.ContextType;
import org.fastj.thunder.context.TestThunderEvent;
import org.fastj.thunder.context.ThunderEvent;
import org.junit.Assert;

import java.util.Optional;

public class MockClassCodeCompleterTest extends LightJavaCodeInsightFixtureTestCase {

    public void testMockClass() {
        PsiFile file = myFixture.configureByFile("main/java/thunder/mockclass/MockClassCompleterTest.java");
        ThunderEvent event = new TestThunderEvent(myFixture);
        Optional<? extends CodeCompleter> codeCompleter = CodeCompleterFactory.getInstance().create(event, ContextType.MOCK_CLASS);
        codeCompleter.ifPresent(CodeCompleter::tryComplete);
        PsiClass psiClass = ((PsiJavaFile)file).getClasses()[1];
        PsiMethod method = psiClass.findMethodsByName("test", true)[0];
        PsiCodeBlock body = method.getBody();
        PsiStatement statement = PsiTreeUtil.getChildOfType(body, PsiStatement.class);
        Assert.assertTrue(statement.getText().contains("MockingClass mockingClass = org.mockito.Mockito.mock(MockingClass.class);"));
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

}
