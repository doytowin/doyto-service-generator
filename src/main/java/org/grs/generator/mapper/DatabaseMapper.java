package org.grs.generator.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.grs.generator.model.Column;

/**
 * 类描述
 *
 * @author Yuan Zhen on 2016-07-23.
 */
@Mapper
public interface DatabaseMapper {

    @Select("DESC ${table}")
    List<Column> listColumns(@Param("table") String table);

    @Update("${sql}")
    void createTable(@Param("sql") String sql);

    @Select("DROP TABLE IF EXISTS ${table}")
    void dropTable(@Param("table") String table);
}
