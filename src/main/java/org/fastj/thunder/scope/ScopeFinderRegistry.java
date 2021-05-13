package org.fastj.thunder.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScopeFinderRegistry {

    private final static ScopeFinderRegistry scopeFinderRegistry = new ScopeFinderRegistry();

    private final static Map<Scope, ScopeMatcher> scopeFinderMap = new ConcurrentHashMap<>();

    public static ScopeFinderRegistry getInstance() {
        return scopeFinderRegistry;
    }

    private ScopeMatcher buildScopeFinder(Scope scope) {
        switch (scope) {
            case UNIT_TEST_CLASS:
                return new UnitTestClassScopeMatcher();
            case BUILDER:
                return new BuilderScopeMatcher();
            case REPOSITORY:
                return new RepositoryScopeMatcher();
            default:
            throw new IllegalArgumentException("Bad scope " + scope);
        }
    }

    public ScopeMatcher getScopeFinder(Scope scope) {
        if (!scopeFinderMap.containsKey(scope)) {
            ScopeMatcher scopeMatcher = buildScopeFinder(scope);
            scopeFinderMap.putIfAbsent(scope, scopeMatcher);
        }
        return scopeFinderMap.get(scope);
    }

}
