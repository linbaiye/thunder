package org.fastj.thunder.scope;

public abstract class AbstractScopeMatcher implements ScopeMatcher {

    private final ScopeMatcher next;

    public AbstractScopeMatcher(ScopeMatcher next) {
        this.next = next;
    }

    protected abstract ScopeType doMatch(ThunderEvent thunderEvent);

    @Override
    public ScopeType match(ThunderEvent thunderEvent) {
        ScopeType scopeType = doMatch(thunderEvent);
        if (scopeType != null) {
            return scopeType;
        }
        return next != null ? next.match(thunderEvent) : ScopeType.UNKNOWN;
    }
}
