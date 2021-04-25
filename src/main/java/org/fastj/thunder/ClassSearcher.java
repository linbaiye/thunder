package org.fastj.thunder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.ProjectScope;

public interface ClassSearcher {

    default PsiClass searchClass(Project project, String qualifiedName) {
        return JavaPsiFacade.getInstance(project).findClass(qualifiedName, ProjectScope.getProjectScope(project));
    }
}
