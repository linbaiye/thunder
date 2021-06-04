package completer.mybatis;

public interface BaseMapper<T> {

    T selectOne(Object wrapper);

    List<T> selectList(Object wrapper);

}