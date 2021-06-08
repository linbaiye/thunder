package org.fastj.thunder.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Assert;

public class CompletionContributorTest extends LightJavaCodeInsightFixtureTestCase {


    private void assertContainsLookup(LookupElement[] lookupElements, String target) {
        boolean found = false;
        for (LookupElement lookupElement : lookupElements) {
            if (target.equals(lookupElement.getLookupString())) {
                found = true;
            }
        }
        Assert.assertTrue(found);
    }

    private void assertContainsThunderLookup(LookupElement[] lookupElements) {
        assertContainsLookup(lookupElements, "thunder");
    }

    public void testBuilderSuggestion() {
        myFixture.configureByFiles("builder/TestCaretAtDeclaration.java", "builder/TestClass.java");
        LookupElement[] lookupElements = myFixture.completeBasic();
        assertContainsThunderLookup(lookupElements);
    }

    public void testMybatisMethodSuggestion() {
        myFixture.configureByFile("main/java/thunder/MybatisMethodParamterSuggestionTest.java");
        LookupElement[] lookupElements = myFixture.completeBasic();
        assertContainsLookup(lookupElements, "lambda");
    }

    public void testMockSuggestion() {
        myFixture.configureByFile("test/java/mock/MockitoContributorTest.java");
        LookupElement[] lookupElements = myFixture.completeBasic();
        assertContainsLookup(lookupElements, "mock");
    }

    public void testLowerCaseMockSuggestion() {
        myFixture.configureByFile("test/java/mock/MockitoLowerCaseContributorTest.java");
        LookupElement[] lookupElements = myFixture.completeBasic();
        Assert.assertEquals(0, lookupElements.length);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

}
