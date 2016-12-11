package org.grs.generator.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.grs.generator.model.Column;

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface ColumnMapper {
    String Table = "gen_column";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    /* ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ */
    @Select(LIST + " WHERE tableName = #{tableName}")
    List<Column> getByTableName(String tableName);

    @Delete(DELETE + " WHERE tableName = #{tableName}")
    void deleteByTableName(String tableName);

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
    /* ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲ */

    @Select(LIST + _WHERE_ID)
    Column get(Serializable id);

    @Delete(DELETE + _WHERE_ID)
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

    @SelectProvider(type = ColumnSqlProvider.class, method = "count")
    int count(Column record);

    class ColumnSqlProvider {
        private String queryOrCount(Column record, boolean query) {
            return new SQL() {{
                SELECT(query ? "*" : "COUNT(*)");
                FROM(Table);
                if (record.getTableName() != null) {
                    WHERE("tableName = #{tableName}");
                }
            }}.toString() + (query && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query(Column record) {
            return queryOrCount(record, true);
        }

        public String count(Column record) {
            return queryOrCount(record, false);
        }

        public String update(final Column record) {
            return new SQL() {{
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
                    SET("`key` = #{key,jdbcType=VARCHAR}");
                }
                WHERE("id = #{id,jdbcType=INTEGER}");
            }}.toString();
        }
    }
}