package org.fastj.thunder.context;

import java.util.Set;

/**
 * A ContextMatcher is responsible for figuring out within what context
 * the plug-in was triggered.
 */
public interface ContextMatcher {

    default ContextType match(ThunderEvent thunderEvent) {
        return ContextType.UNKNOWN;
    }

    /**
     * Add corresponding ContextType to the result if there is a match.
     * @param event
     * @param result
     */
    default void addIfMatch(ThunderEvent event, Set<ContextType> result) { }

}
