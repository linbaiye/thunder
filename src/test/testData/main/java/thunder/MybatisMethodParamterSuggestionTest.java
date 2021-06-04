package thunder;


class MyEntityMapper {
    public Object selectOne(Object plan);
}

public class MybatisMethodParamterSuggestionTest {

    private MyEntityMapper myEntityMapper;

    public void testSelectOne() {
        myEntityMapper.selectOne(l<caret>)
    }

}