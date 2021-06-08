package org.fastj.thunder.context;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ContextMatcherFactory {

    private final static ContextMatcherFactory CONTEXT_MATCHER_FACTORY = new ContextMatcherFactory();

    private static final List<Class<? extends ContextMatcher>> CLASSES = Arrays.asList(
            ValidationContextMatcher.class, UnitTestClassContextMatcher.class
    );

    private volatile List<ContextMatcher> contextMatcherList;

    public static ContextMatcherFactory getInstance() {
        return CONTEXT_MATCHER_FACTORY;
    }

    private synchronized void buildMatchers() {
        try {
            if (contextMatcherList != null) {
                return;
            }
            contextMatcherList = new LinkedList<>();
            for (Class<? extends ContextMatcher> matcherClass : CLASSES) {
                Constructor<? extends ContextMatcher> constructor = matcherClass.getConstructor();
                contextMatcherList.add(constructor.newInstance());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to build context matchers.");
        }
    }

    public List<? extends ContextMatcher> getMatchers() {
        if (contextMatcherList == null) {
            buildMatchers();
        }
        return contextMatcherList;
    }
}
