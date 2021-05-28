package org.fastj.thunder.scope;

import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.modifier.builder.BuilderCodeModifier;
import org.fastj.thunder.modifier.builder.BuilderScopeParser;
import org.fastj.thunder.modifier.builder.SimpleParameterSelector;
import org.junit.Assert;

public class BuilderCodeModifierTest extends LightJavaCodeInsightFixtureTestCase {

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
        BuilderScopeParser builderScopeParser = new BuilderScopeParser(event);
        BuilderCodeModifier builderCodeModifier = new BuilderCodeModifier(builderScopeParser, new SimpleParameterSelector(builderScopeParser.getSourceParameterCandidates()));
        builderCodeModifier.tryModify();
        assertModifiedClass(files[0]);
    }

    public void testModifyExpressionStatement() {
        PsiFile[] files = myFixture.configureByFiles("builder/TestExpressionStatement.java", "builder/TestClass.java");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        BuilderScopeParser builderScopeParser = new BuilderScopeParser(event);
        BuilderCodeModifier builderCodeModifier = new BuilderCodeModifier(builderScopeParser, new SimpleParameterSelector(builderScopeParser.getSourceParameterCandidates()));
        builderCodeModifier.tryModify();
        assertModifiedClass(files[0]);
    }

    public void testModifyInsideLambda() {
        PsiFile[] files = myFixture.configureByFiles("builder/TestInsideLambda.java", "builder/TestClass.java");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        BuilderScopeParser builderScopeParser = new BuilderScopeParser(event);
        BuilderCodeModifier builderCodeModifier = new BuilderCodeModifier(builderScopeParser, new SimpleParameterSelector(builderScopeParser.getSourceParameterCandidates()));
        builderCodeModifier.tryModify();
        assertModifiedClass(files[0]);
    }

    public void testModifyReturnStatement() {
        PsiFile[] files = myFixture.configureByFiles("builder/TestReturnStatement.java", "builder/TestClass.java");
        TestThunderEvent event = new TestThunderEvent(myFixture);
        BuilderScopeParser builderScopeParser = new BuilderScopeParser(event);
        BuilderCodeModifier builderCodeModifier = new BuilderCodeModifier(builderScopeParser, new SimpleParameterSelector(builderScopeParser.getSourceParameterCandidates()));
        builderCodeModifier.tryModify();
        assertModifiedClass(files[0]);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }
}
