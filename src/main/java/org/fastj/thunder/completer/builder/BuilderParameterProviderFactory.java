package org.fastj.thunder.completer.builder;

public class BuilderParameterProviderFactory {

    private static final BuilderParameterProviderFactory INSTANCE = new BuilderParameterProviderFactory();

    public static BuilderParameterProviderFactory getInstance() {
        return INSTANCE;
    }

    public BuilderParameterProvider create(LombokBuilderContextAnalyser parser) {
        BuilderParameterProvider similar = new SimilarityBuilderParameterProvider(null, parser);
        return new SimpleBuilderParameterProvider(similar, parser);
    }


}
