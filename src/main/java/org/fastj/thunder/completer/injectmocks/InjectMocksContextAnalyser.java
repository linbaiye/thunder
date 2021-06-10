package org.fastj.thunder.completer.injectmocks;

import com.intellij.psi.*;
import com.intellij.psi.search.ProjectScope;
import org.fastj.thunder.completer.AbstractContextAnalyser;
import org.fastj.thunder.context.ThunderEvent;

public class InjectMocksContextAnalyser extends AbstractContextAnalyser {

    /**
     * The class being tested.
     */
    private final PsiClass testedClass;

    private final PsiClass unitTestClass;

    public InjectMocksContextAnalyser(ThunderEvent thunderEvent) {
        super(thunderEvent);
        unitTestClass = findUnitTestClass();
        testedClass = findTestedClass();
    }

    private PsiClass findUnitTestClass() {
        PsiFile file = thunderEvent.getFile();
        return ((PsiJavaFile)file).getClasses()[0];
    }


    private PsiClass findTestedClass() {
        String testedClassQualifiedName = unitTestClass.getQualifiedName().replaceAll("^(.*)UT$", "$1");
        return JavaPsiFacade.getInstance(thunderEvent.getProject())
                .findClass(testedClassQualifiedName, ProjectScope.getProjectScope(thunderEvent.getProject()));
    }

    public PsiField[] getFieldsOfTestedClass() {
        return testedClass.getAllFields();
    }

    public PsiField[] getFieldsOfUnitTestClass() {
        return unitTestClass.getAllFields();
    }

    public PsiClass getTestedClass() {
        return testedClass;
    }

    public PsiClass getUnitTestClass() {
        return unitTestClass;
    }
}
