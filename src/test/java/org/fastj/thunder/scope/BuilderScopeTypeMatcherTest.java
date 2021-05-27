package org.fastj.thunder.scope;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Assert;

public class BuilderScopeTypeMatcherTest extends LightJavaCodeInsightFixtureTestCase {


    public void testMatchDeclarationStatementBuilder() {
        myFixture.configureByText("DeclarationStatementBuilder.java",
                "public class DeclarationStatementBuilder {" +
                "public void test() {" +
                "Hello hello = Hello.builder()<caret> " +
                        "}" +
                        "}");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        BuilderScopeMatcher matcher = new BuilderScopeMatcher(null);
        Assert.assertSame(matcher.match(event), ScopeType.BUILDER);
    }

    public void testMatchMethodCallExpressionBuilder() {
        myFixture.configureByText("DeclarationStatementBuilder.java",
                "public class DeclarationStatementBuilder {" +
                        "public void test() {" +
                        "Hello.builder()<caret> " +
                        "}" +
                        "}");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        BuilderScopeMatcher matcher = new BuilderScopeMatcher(null);
        Assert.assertSame(matcher.match(event), ScopeType.BUILDER);
    }

    public void testMatchInsideLambda() {
        myFixture.configureByText("DeclarationStatementBuilder.java",
                "public class DeclarationStatementBuilder {" +
                        "public void test(List<World> worldList) {" +
                        "worldList.stream().map(e -> Hello.builder()<caret>) " +
                        "}" +
                        "}");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        BuilderScopeMatcher matcher = new BuilderScopeMatcher(null);
        Assert.assertSame(matcher.match(event), ScopeType.BUILDER);
    }
}
