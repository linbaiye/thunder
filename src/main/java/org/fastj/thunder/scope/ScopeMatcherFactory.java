package org.fastj.thunder.scope;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class ScopeMatcherFactory {

    private final static ScopeMatcherFactory SCOPE_MATCHER_REGISTRY = new ScopeMatcherFactory();

    private volatile ScopeMatcher head;

    private static final List<Class<? extends ScopeMatcher>> CLASS_LIST = Arrays.asList(
            BuilderScopeMatcher.class, RepositoryScopeMatcher.class, UnitTestClassScopeMatcher.class
    );

    public static ScopeMatcherFactory getInstance() {
        return SCOPE_MATCHER_REGISTRY;
    }

    private synchronized void buildMatcherChain() {
        try {
            ScopeMatcher current = null;
            for (Class<? extends ScopeMatcher> matcherClass : CLASS_LIST) {
                Constructor<? extends ScopeMatcher> constructor = matcherClass.getConstructor(ScopeMatcher.class);
                current = constructor.newInstance(current);
            }
            head = current;
        } catch (Exception e) {
            throw new RuntimeException("Failed build scope matcher.");
        }
    }

    public ScopeMatcher getOrCreate() {
        if (head == null) {
            buildMatcherChain();
        }
        return head;
    }
}
