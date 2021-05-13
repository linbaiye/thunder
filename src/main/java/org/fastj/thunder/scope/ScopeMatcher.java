package org.fastj.thunder.scope;

import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * A ScopeMatcher is responsible for figuring out inside what scope
 * the plug-in was triggered.
 */
public interface ScopeMatcher {

    Scope match(AnActionEvent actionEvent);

}
