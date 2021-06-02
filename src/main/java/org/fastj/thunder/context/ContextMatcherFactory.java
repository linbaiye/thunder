package org.fastj.thunder.context;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class ContextMatcherFactory {

    private final static ContextMatcherFactory SCOPE_MATCHER_REGISTRY = new ContextMatcherFactory();

    private volatile ContextMatcher head;

    private static final List<Class<? extends ContextMatcher>> CLASS_LIST = Arrays.asList(
            BuilderContextMatcher.class, RepositoryContextMatcher.class, UnitTestClassContextMatcher.class
    );

    public static ContextMatcherFactory getInstance() {
        return SCOPE_MATCHER_REGISTRY;
    }

    private synchronized void buildMatcherChain() {
        try {
            ContextMatcher current = null;
            for (Class<? extends ContextMatcher> matcherClass : CLASS_LIST) {
                Constructor<? extends ContextMatcher> constructor = matcherClass.getConstructor(ContextMatcher.class);
                current = constructor.newInstance(current);
            }
            head = current;
        } catch (Exception e) {
            throw new RuntimeException("Failed build scope matcher.");
        }
    }

    public ContextMatcher getOrCreate() {
        if (head == null) {
            buildMatcherChain();
        }
        return head;
    }
}
