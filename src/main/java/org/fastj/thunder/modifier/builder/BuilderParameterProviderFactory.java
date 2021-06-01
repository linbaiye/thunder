package org.fastj.thunder.modifier.builder;

public class BuilderParameterProviderFactory {

    private static final BuilderParameterProviderFactory INSTANCE = new BuilderParameterProviderFactory();

    public static BuilderParameterProviderFactory getInstance() {
        return INSTANCE;
    }

    public BuilderParameterProvider create(LombokBuilderContextParser parser) {
        BuilderParameterProvider similar = new SimilarityBuilderParameterProvider(null, parser);
        return new SimpleBuilderParameterProvider(similar, parser);
    }


}
