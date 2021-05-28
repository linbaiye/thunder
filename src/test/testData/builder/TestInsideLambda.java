package builder;

import java.util.List;

public class TestInsideLambda {
    public void testBuilder(List<String> stringList, String key1, String key2) {
        stringList.stream().map(e -> TestClass.builder().<caret>)
    }
}
