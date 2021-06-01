package org.fastj.thunder.modifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.modifier.builder.LombokBuilderContextParser;
import org.fastj.thunder.modifier.builder.SimilarityBuilderParameterProvider;
import org.fastj.thunder.scope.TestThunderEvent;
import org.junit.Assert;


public class SimilarityBuilderParameterProviderTest extends LightJavaCodeInsightFixtureTestCase {

    public void testSimilarityMatch() {
        PsiJavaFile file = (PsiJavaFile) myFixture.configureByFile("builder/TestFuzzyClassMatch.java");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        LombokBuilderContextParser lombokBuilderScopeParser = new LombokBuilderContextParser(event);
        SimilarityBuilderParameterProvider provider = new SimilarityBuilderParameterProvider(null, lombokBuilderScopeParser);
        String exp = provider.provideParameterExpression("key1");
        Assert.assertEquals("k1", exp);
        exp = provider.provideParameterExpression("setJustALittleWeiredName");
        Assert.assertEquals("weired", exp);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }
}
