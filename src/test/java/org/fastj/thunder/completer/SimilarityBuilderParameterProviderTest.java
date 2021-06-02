package org.fastj.thunder.completer;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.completer.builder.LombokBuilderContextAnalyser;
import org.fastj.thunder.completer.builder.SimilarityBuilderParameterProvider;
import org.fastj.thunder.context.TestThunderEvent;
import org.junit.Assert;


public class SimilarityBuilderParameterProviderTest extends LightJavaCodeInsightFixtureTestCase {

    public void testSimilarityMatch() {
        PsiJavaFile file = (PsiJavaFile) myFixture.configureByFile("builder/TestFuzzyClassMatch.java");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        LombokBuilderContextAnalyser lombokBuilderScopeParser = new LombokBuilderContextAnalyser(event);
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
