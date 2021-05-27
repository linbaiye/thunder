package org.fastj.thunder.modifier;

import org.fastj.thunder.scope.ThunderEvent;

public abstract class AbstractScopeParser {

    protected final ThunderEvent thunderEvent;

    public AbstractScopeParser(ThunderEvent thunderEvent) {
        this.thunderEvent = thunderEvent;
    }
}
