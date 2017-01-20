package org.grs.generator.component.mybatis;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;

/**
 * IMapper
 *
 * @author f0rb on 2017-01-15.
 */
public interface IMapper<T> {

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _LIMIT_1 = " LIMIT 1" + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    String LIST_ = "SELECT * FROM ";
    String LOAD_ = "SELECT id FROM ";
    String DELETE_ = "DELETE FROM ";
    String COUNT_ = "SELECT COUNT(*) FROM ";
    String HAS_ = "SELECT COUNT(*) > 0 FROM ";

    String Table = "@{table}";

    @Select(LIST_ + Table + _WHERE_ID)
    T get(@Param("id") Serializable id);

    @Delete(DELETE_ + Table + _WHERE_ID)
    int delete(@Param("id") Serializable id);

    int insert(T t);

    int update(T t);

    List<T> query(T t);

    long count(T t);

    @Flush
    void flush();

}
