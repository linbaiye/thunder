package org.fastj.thunder.completer.mybatis;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNewExpression;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.completer.CodeCompleter;
import org.fastj.thunder.completer.CodeCompleterFactory;
import org.fastj.thunder.context.ContextType;
import org.fastj.thunder.context.TestThunderEvent;
import org.fastj.thunder.context.ThunderEvent;
import org.junit.Assert;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MybatisMethodParameterCompleterTest extends LightJavaCodeInsightFixtureTestCase {

    public void testCompleteParameter() {
        PsiFile[] files = myFixture.configureByFiles("completer/mybatis/MyEntityService.java",
                "completer/mybatis/MyEntityMapper.java",
                "completer/mybatis/MyEntity.java", "completer/mybatis/BaseMapper.java");
        ThunderEvent event = new TestThunderEvent(myFixture);
        Optional<? extends CodeCompleter> codeCompleter = CodeCompleterFactory.getInstance().create(event, ContextType.MYBATIS_METHOD_PARAMETER);
        codeCompleter.ifPresent(CodeCompleter::tryComplete);
        AtomicReference<Boolean> found = new AtomicReference<>();
        event.getElementAtCaret().getParent().acceptChildren(new JavaRecursiveElementVisitor() {
            @Override
            public void visitNewExpression(PsiNewExpression expression) {
                Assert.assertEquals("new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MyEntity>()",
                        expression.getText());
                found.set(true);
            }
        });
        Assert.assertTrue(found.get());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

}
