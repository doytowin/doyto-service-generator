package org.grs.generator.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.grs.generator.model.Column;

@Mapper
//@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface ColumnMapper {
    String Table = "GEN_Column";
    String LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String LIMIT_OFFSET = LIMIT + _OFFSET;
    String WHERE_ID = " WHERE id = #{id}";

    String LIST_ = "SELECT * FROM ";
    String DELETE_ = "DELETE FROM ";
    String HAS_ = "SELECT COUNT(*) > 0 FROM ";

    String LIST = LIST_ + Table;
    String HAS = HAS_ + Table;

    @Select(LIST + WHERE_ID)
    Column get(Serializable id);

    @Delete(DELETE_ + Table + WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
        "insert into",
        Table,
        "(`tableName`,`field`,`label`,`type`,`nullable`,`key`)",
        "values",
        "(#{tableName},#{field},#{label},#{type},#{nullable},#{key})"
    })
    int insert(Column record);

    @UpdateProvider(type = ColumnSqlProvider.class, method = "update")
    int update(Column record);

    @Update(value = {
            "<script>",
            "<foreach collection='list' item='col' separator=';'>",
            "UPDATE",
            Table,
            "SET label = #{col.label}",
            "WHERE id = #{col.id}",
            "</foreach>",
            "</script>"
    })
    @Options
    int updateLabels(@Param("list") List<Column> list);

    @Insert(value = {
            "<script>",
            "insert into",
            Table,
            "(`id`,`tableName`,`field`,`label`,`type`,`nullable`,`key`)",
            "values",
            "<foreach collection='list' item='col' index='index' separator=','>",
            "(#{col.id},#{col.tableName},#{col.field},#{col.label},#{col.type},#{col.nullable},#{col.key})",
            "</foreach>",
            "ON DUPLICATE KEY UPDATE",
            "label = VALUES(label)",
            "</script>"
    })
    @Options(useGeneratedKeys = true)
    int saveColumns(@Param("list") List<Column> list);

    /**
     * 检查某列是否存在某值
     *
     * @param column 列名
     * @param value  待检值
     * @return 如果值存在, 则返回true; 否则返回false
     */
    @Select(HAS + " WHERE ${column} = #{value}")
    @Options(useCache = false)
    Boolean hasValueOnColumn(@Param("column") String column, @Param("value") String value);

    @SelectProvider(type = ColumnSqlProvider.class, method = "query")
    List<Column> query(Column record);

    @Select(LIST + " WHERE tableName = #{tableName}")
    List<Column> getByTableName(String tableName);

    @SelectProvider(type = ColumnSqlProvider.class, method = "count")
    int count(Column record);

    @Delete(DELETE_ + Table + " WHERE tableName = #{tableName}")
    void deleteByTableName(String tableName);

    class ColumnSqlProvider {
        private String queryOrCount(Column record, boolean select) {
            return new SQL() {
                {
                    SELECT(select ? "*" : "COUNT(*)");
                    FROM(Table);
                    //if (record.getName() != null) {
                    //    WHERE("name like CONCAT('%',#{name},'%')");
                    //}
                }
            }.toString() + (select && record.needPaging() ? LIMIT_OFFSET : "");
        }

        public String query(Column record) {
            return queryOrCount(record, true);
        }

        public String count(Column record) {
            return queryOrCount(record, false);
        }

        public String update(final Column record) {
            return new SQL() {
                {
                    UPDATE(Table);
                    if (record.getTableName() != null) {
                        SET("`tableName` = #{tableName,jdbcType=VARCHAR}");
                    }
                    if (record.getField() != null) {
                        SET("`field` = #{field,jdbcType=VARCHAR}");
                    }
                    if (record.getLabel() != null) {
                        SET("`label` = #{label,jdbcType=VARCHAR}");
                    }
                    if (record.getType() != null) {
                        SET("`type` = #{type,jdbcType=VARCHAR}");
                    }
                    if (record.getNullable() != null) {
                        SET("`nullable` = #{nullable,jdbcType=BIT}");
                    }
                    if (record.getKey() != null) {
                        SET("`key` = #{key,jdbcType=BIT}");
                    }
                    WHERE("id = #{id,jdbcType=INTEGER}");
                }
            }.toString();
        }

        //public String updateLabels(final List<Column> list) {
        //    return new SQL() {
        //        {
        //            UPDATE(Table);
        //            if (record.getLabel() != null) {
        //                SET("`label` = #{label,jdbcType=VARCHAR}");
        //            }
        //            WHERE("id = #{id,jdbcType=INTEGER}");
        //        }
        //    }.toString();
        //}
    }
}