package completer.mybatis;

public class EntityService {

    private MyEntityMapper myEntityMapper;

    public void testSelectOne() {
        myEntityMapper.selectOne(l<caret>)
    }

}