package org.fastj.thunder.completer;

import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.completer.builder.BuilderCodeCompleter;
import org.fastj.thunder.completer.builder.LombokBuilderContextAnalyser;
import org.fastj.thunder.completer.builder.SimpleBuilderParameterProvider;
import org.fastj.thunder.context.TestThunderEvent;
import org.junit.Assert;

public class BuilderCodeCompleterTest extends LightJavaCodeInsightFixtureTestCase {

    private void assertModifiedClass(PsiFile psiFile) {
        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
        System.out.println(javaFile.getText());
        PsiClass psiClass = javaFile.getClasses()[0];
        PsiMethod psiMethod = psiClass.findMethodsByName("testBuilder", false)[0];
        Assert.assertTrue(psiMethod.getText().replaceAll("\\s+", "").contains(".key1(key1).key2(key2).build()"));
    }

    public void testModifyDeclarationStatement() {
        PsiFile[] files = myFixture.configureByFiles("builder/TestCaretAtDeclaration.java", "builder/TestClass.java");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        LombokBuilderContextAnalyser lombokBuilderScopeParser = new LombokBuilderContextAnalyser(event);
        BuilderCodeCompleter builderCodeModifier = new BuilderCodeCompleter(lombokBuilderScopeParser, new SimpleBuilderParameterProvider(lombokBuilderScopeParser.getSourceParameterCandidates()));
        builderCodeModifier.tryComplete();
        assertModifiedClass(files[0]);
    }

    public void testModifyExpressionStatement() {
        PsiFile[] files = myFixture.configureByFiles("builder/TestExpressionStatement.java", "builder/TestClass.java");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        LombokBuilderContextAnalyser lombokBuilderScopeParser = new LombokBuilderContextAnalyser(event);
        BuilderCodeCompleter builderCodeModifier = new BuilderCodeCompleter(lombokBuilderScopeParser, new SimpleBuilderParameterProvider(lombokBuilderScopeParser.getSourceParameterCandidates()));
        builderCodeModifier.tryComplete();
        assertModifiedClass(files[0]);
    }

    public void testModifyInsideLambda() {
        PsiFile[] files = myFixture.configureByFiles("builder/TestInsideLambda.java", "builder/TestClass.java");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        LombokBuilderContextAnalyser lombokBuilderScopeParser = new LombokBuilderContextAnalyser(event);
        BuilderCodeCompleter builderCodeModifier = new BuilderCodeCompleter(lombokBuilderScopeParser, new SimpleBuilderParameterProvider(lombokBuilderScopeParser.getSourceParameterCandidates()));
        builderCodeModifier.tryComplete();
        assertModifiedClass(files[0]);
    }

    public void testModifyReturnStatement() {
        PsiFile[] files = myFixture.configureByFiles("builder/TestReturnStatement.java", "builder/TestClass.java");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        LombokBuilderContextAnalyser lombokBuilderScopeParser = new LombokBuilderContextAnalyser(event);
        BuilderCodeCompleter builderCodeModifier = new BuilderCodeCompleter(lombokBuilderScopeParser, new SimpleBuilderParameterProvider(lombokBuilderScopeParser.getSourceParameterCandidates()));
        builderCodeModifier.tryComplete();
        assertModifiedClass(files[0]);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }
}
