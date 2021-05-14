package org.fastj.thunder.modifier.builder;

import com.intellij.psi.PsiType;
import org.fastj.thunder.modifier.CodeModifier;

import java.util.Map;

public class BuilderCodeModifier implements CodeModifier {

    private final BuilderContextParser contextParser;

    public BuilderCodeModifier(BuilderContextParser contextParser) {
        this.contextParser = contextParser;
    }



    @Override
    public void tryModify() {
        Map<String, PsiType> candidates = contextParser.parseBuilderSourceParameterCandidates();

    }
}
