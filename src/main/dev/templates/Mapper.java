package org.grs.generator.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.grs.generator.model.{{gen.modelName}};

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface {{gen.modelName}}Mapper {
    String Table = "{{gen.tableName}}";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    /* ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ */
    /* ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲ */

    @Select(LIST + _WHERE_ID)
    {{gen.modelName}} get(Serializable id);

    @Delete(DELETE + _WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
            "insert into",
            Table,
            "(<span ng-repeat="column in columns | regex:'field':'^(?!id$|reply|createTime|update|valid)'">`{{column.field}}`{{!$last ? ',' : ''}}</span>)",
            "values",
            "(<span ng-repeat="column in columns | regex:'field':'^(?!id$|reply|createTime|update|valid)'">#<code>{</code>{{column.field}}}{{!$last ? ',' : ''}}</span>)"
    })
    int insert({{gen.modelName}} record);

    @UpdateProvider(type = {{gen.modelName}}SqlProvider.class, method = "update")
    int update({{gen.modelName}} record);

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

    @SelectProvider(type = {{gen.modelName}}SqlProvider.class, method = "query")
    List&lt;{{gen.modelName}}&gt; query({{gen.modelName}} record);

    @SelectProvider(type = {{gen.modelName}}SqlProvider.class, method = "count")
    int count({{gen.modelName}} record);

    class {{gen.modelName}}SqlProvider {
        private String queryOrCount({{gen.modelName}} record, boolean query) {
            return new SQL() <code ng-non-bindable>{{</code>
                SELECT(query ? "*" : "COUNT(*)");
                FROM(Table);
                //if (record.getId() != null) {
                //    WHERE("id = #{id}");
                //}
                //if (record.getName() != null) {
                //    WHERE("name like CONCAT('%',#{name},'%')");
                //}
                //if (query) {
                //    ORDER_BY("createTime desc");
                //}
            }}.toString() + (query && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query({{gen.modelName}} record) {
            return queryOrCount(record, true);
        }

        public String count({{gen.modelName}} record) {
            return queryOrCount(record, false);
        }

        public String update(final {{gen.modelName}} record) {
            return new SQL() <code ng-non-bindable>{{</code>
                UPDATE(Table);<ng-repeat ng-repeat="column in columns | regex:'field':'^(?!id$|create)'">
                if (record.get{{column.field | capitalize}}() != null) {
                    SET("`{{column.field}}` = #<code>{</code>{{column.field}},jdbcType={{column.jdbcType}}}");
                }</ng-repeat>
                WHERE("id = #{id,jdbcType=INTEGER}");
            }}.toString();
        }
    }
}