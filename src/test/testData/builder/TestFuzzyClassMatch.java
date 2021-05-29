
package builder;


class AnotherClass {
    private String name;

    public String getName() {
        return name;
    }
}

public class TestFuzzyClassMatch {

    private String key1;

    private String key2;

    private String justALittleWeiredName;

    private Integer shortName;

    private String nameInAnotherClass;

    public TestFuzzyClassMatch(String key1, String key2, String justALittleWeiredName, Integer shortName, String nameInAnotherClass) {
        this.key1 = key1;
        this.key2 = key2;
        this.justALittleWeiredName = justALittleWeiredName;
        this.shortName = shortName;
        this.nameInAnotherClass = nameInAnotherClass;
    }

    public static class TestClassBuilder {
        private String key1;
        private String key2;
        private String justALittleWeiredName;
        private Integer shortName;
        private String nameInAnotherClass;

        public TestClassBuilder setKey1(String key1) {
            this.key1 = key1;
            return this;
        }

        public TestClassBuilder setKey2(String key2) {
            this.key2 = key2;
            return this;
        }

        public TestClassBuilder setJustALittleWeiredName(String justALittleWeiredName) {
            this.justALittleWeiredName = justALittleWeiredName;
            return this;
        }

        public TestClassBuilder setShortName(Integer shortName) {
            this.shortName = shortName;
            return this;
        }

        public TestClassBuilder setNameInAnotherClass(String nameInAnotherClass) {
            this.nameInAnotherClass = nameInAnotherClass;
            return this;
        }

        public TestFuzzyClassMatch build() {
            return new TestFuzzyClassMatch(key1, key2, justALittleWeiredName, shortName, nameInAnotherClass);
        }
    }

    public static TestClassBuilder builder() {
        return new TestClassBuilder();
    }

    public static TestClassBuilder create(String k1, String k2, AnotherClass anotherClass, String name, String weired) {
        TestFuzzyClassMatch.builder().<caret>
    }

}