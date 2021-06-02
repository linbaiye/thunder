package org.fastj.thunder.completer.mybatis;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.fastj.thunder.completer.CodeCompleter;

public class MybatisMethodParameterCompleter implements CodeCompleter {

    private final RepositoryContextAnalyser contextParser;

    public MybatisMethodParameterCompleter(RepositoryContextAnalyser contextParser) {
        this.contextParser = contextParser;
    }

    private boolean isCompletable() {
        return contextParser.getEntityClass() != null &&
                contextParser.getIdentifier() != null &&
                contextParser.getRepositoryClass() != null;
    }

    private void complete() {
        PsiClass entityClass = contextParser.getEntityClass();
        String builder = "new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<" + entityClass.getQualifiedName() +
                ">()";
        PsiElement element = PsiElementFactory.getInstance(contextParser.getProject()).createExpressionFromText(builder,
                contextParser.getIdentifier().getParent());
        WriteCommandAction.runWriteCommandAction(contextParser.getProject(), () -> {
            PsiElement expression = JavaCodeStyleManager.getInstance(contextParser.getProject()).shortenClassReferences(element);
            contextParser.getIdentifier().replace(expression);
        });
    }

    @Override
    public void tryComplete() {
        if (isCompletable()) {
            complete();
        }
    }
}
