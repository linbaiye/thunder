package org.fastj.thunder.scope;

/**
 * A ScopeMatcher is responsible for figuring out inside what scope
 * the plug-in was triggered.
 */
public interface ScopeMatcher {

    ScopeType match(ThunderEvent thunderEvent);

}
