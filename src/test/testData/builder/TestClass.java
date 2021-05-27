
package builder;

public class TestClass {

    private String key1;

    private String key2;

    public static class TestClassBuilder {

        private String key1;

        private String key2;

        public TestClass.TestClassBuilder key1(String k1) {
            key1 = k1;
            return this;
        }

        public TestClass.TestClassBuilder key2(String k2) {
            key2 = k2;
            return this;
        }

        public TestClass build() {
            return new TestClass();
        }
    }

    public static TestClass.TestClassBuilder builder() {
        return new TestClass.TestClassBuilder();
    }
}