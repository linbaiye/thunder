package org.fastj.thunder.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiFile;
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

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

}
