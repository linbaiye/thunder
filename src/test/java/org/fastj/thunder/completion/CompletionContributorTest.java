package org.fastj.thunder.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Assert;

public class CompletionContributorTest extends LightJavaCodeInsightFixtureTestCase {


    private void assertContainsThunderLookup(LookupElement[] lookupElements) {
        boolean found = false;
        for (LookupElement lookupElement : lookupElements) {
            if ("thunder".equals(lookupElement.getLookupString())) {
                found = true;
            }
        }
        Assert.assertTrue(found);
    }

    public void testBuilderSuggestion() {
        myFixture.configureByFiles("builder/TestCaretAtDeclaration.java", "builder/TestClass.java");
        LookupElement[] lookupElements = myFixture.completeBasic();
        assertContainsThunderLookup(lookupElements);
    }

    public void testRepositorySuggestion() {
        myFixture.configureByText("Test.java", "public class Test {" +
                "private EntityDao entityDao;" +
                "public void test() {" +
                "   entityDao.selectOne(l<caret>)" +
                "}");
        LookupElement[] lookupElements = myFixture.completeBasic();
        if (lookupElements != null) {
            for (LookupElement lookupElement : lookupElements) {
                System.out.println(lookupElement.getLookupString());
            }
        }
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

}
