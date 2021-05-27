

class HelloWorld {
    static HelloWorld builder() {
        return null;
    }
}

public class DeclarationStatementBuilderTest {

    public void test() {
        HelloWorld.builder() <caret>
    }
}