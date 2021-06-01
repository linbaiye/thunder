package org.fastj.thunder.scope;

/**
 * A ContextMatcher is responsible for figuring out inside what scope
 * the plug-in was triggered.
 */
public interface ContextMatcher {

    ContextType match(ThunderEvent thunderEvent);

}
