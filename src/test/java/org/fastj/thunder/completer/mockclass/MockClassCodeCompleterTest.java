package org.fastj.thunder.completer.mockclass;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.ContextType;
import org.fastj.thunder.context.TestThunderEvent;
import org.fastj.thunder.context.ThunderEvent;

import java.util.Optional;

public class MockClassCodeCompleterTest extends LightJavaCodeInsightFixtureTestCase {

    public void testMockClass() {
        PsiFile file = myFixture.configureByFile("main/java/thunder/mockclass/MockClassCompleterTest.java");
        ThunderEvent event = new TestThunderEvent(myFixture);
        Optional<? extends CodeCompleter> codeCompleter = CodeCompleterFactory.getInstance().create(event, ContextType.MOCK_CLASS);
        codeCompleter.ifPresent(CodeCompleter::tryComplete);
        System.out.println(file.getText());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

}
