package org.fastj.thunder.completer.validation;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.fastj.thunder.completer.CodeCompleter;

public class ValidationAnnotationCodeCompleter implements CodeCompleter {

    private final ValidationAnnotationContextAnalyser contextAnalyser;

    public ValidationAnnotationCodeCompleter(ValidationAnnotationContextAnalyser contextAnalyser) {
        this.contextAnalyser = contextAnalyser;
    }

    private boolean hasNotNullAnnotation(PsiField field) {
        // javax.validation.constraints.NotNull
        return field.getAnnotation("javax.validation.constraints.NotNull") != null;
    }

    private boolean isStaticOrFinal(PsiField field) {
        return field.getModifierList() != null &&
                (field.getModifierList().hasExplicitModifier(PsiModifier.STATIC) ||
                        field.getModifierList().hasExplicitModifier(PsiModifier.FINAL));
    }

    @Override
    public void tryComplete() {
        WriteCommandAction.runWriteCommandAction(contextAnalyser.getProject(), () -> {
                    for (PsiField field : contextAnalyser.getJavaFields()) {
                        if (hasNotNullAnnotation(field) || field.getModifierList() == null ||
                        isStaticOrFinal(field)) {
                            continue;
                        }
                        PsiAnnotation annotation = field.getModifierList()
                                .addAnnotation("javax.validation.constraints.NotNull(message = \"" + field.getName() + "不能为空.\")");
                        JavaCodeStyleManager.getInstance(contextAnalyser.getProject()).shortenClassReferences(annotation);
                    }
                }
        );
    }
}
