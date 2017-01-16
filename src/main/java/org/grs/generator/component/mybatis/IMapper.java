package org.grs.generator.component.mybatis;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * IMapper
 *
 * @author f0rb on 2017-01-15.
 */
public interface IMapper<T> {

    String LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String LIMIT_OFFSET = LIMIT + _OFFSET;
    String LIMIT_1 = " LIMIT 1" + _OFFSET;
    String WHERE_ID = " WHERE id = #{id}";
    String LIST_ = "SELECT * FROM ";
    String LOAD_ = "SELECT id FROM ";
    String DELETE_ = "DELETE FROM ";
    String COUNT_ = "SELECT COUNT(*) FROM ";
    String HAS_ = "SELECT COUNT(*) > 0 FROM ";

    T get(@Param("id") Serializable id);

    int delete(@Param("id") Serializable id);

    int insert(T t);

    int update(T t);

    List<T> query(T t);

    long count(T t);

    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    void flush();

}
