package org.fastj.thunder.context;

public abstract class AbstractContextMatcher implements ContextMatcher {

    private final ContextMatcher next;

    public AbstractContextMatcher(ContextMatcher next) {
        this.next = next;
    }

    protected abstract ContextType doMatch(ThunderEvent thunderEvent);

    @Override
    public ContextType match(ThunderEvent thunderEvent) {
        ContextType contextType = doMatch(thunderEvent);
        if (contextType != null) {
            return contextType;
        }
        return next != null ? next.match(thunderEvent) : ContextType.UNKNOWN;
    }
}
