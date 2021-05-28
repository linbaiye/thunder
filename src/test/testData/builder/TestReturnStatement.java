package builder;

public class TestReturnStatement {

    public TestClass testBuilder(String key1, String key2) {
        return  TestClass.builder().<caret>
    }
}
