package org.fastj.thunder.completer.injectmocks;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
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

    private String getQualifiedClassNameTested() {
        return unitTestClass.getQualifiedName().replaceAll("^(.*)UT$", "$1");
    }


    private PsiClass findTestedClass() {
        return JavaPsiFacade.getInstance(thunderEvent.getProject())
                .findClass(getQualifiedClassNameTested(), ProjectScope.getProjectScope(thunderEvent.getProject()));
    }

    private PsiClass getTestedClass() {
        return testedClass;
    }

}
