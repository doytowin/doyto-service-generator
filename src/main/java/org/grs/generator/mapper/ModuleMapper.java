package org.grs.generator.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.grs.generator.model.Module;
import org.grs.generator.model.Project;

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface ModuleMapper {
    String Table = "gen_module";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    @Select(LIST + _WHERE_ID)
    Module get(Serializable id);

    @Select(LIST + _WHERE_ID)
    @Results(value = {
            @Result(column = "projectId", property = "projectId"),
            @Result(
                    column = "projectId", property = "project", javaType = Project.class,
                    one = @One(select = "org.grs.generator.mapper.ProjectMapper.get")
            ),
            @Result(column = "tableName", property = "tableName"),
            @Result(
                    column = "tableName", property = "columns", javaType = List.class,
                    many = @Many(select = "org.grs.generator.mapper.ColumnMapper.getByTableName")
            )
    })
    @Options(useCache = false)
    Module getWithProjectAndColumns(Serializable id);

    @Delete(DELETE + _WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
        "insert into",
        Table,
        "(`projectId`,`name`,`displayName`,`modelName`,`fullName`,`tableName`)",
        "values",
        "(#{projectId},#{name},#{displayName},#{modelName},#{fullName},#{tableName})"
    })
    int insert(Module record);

    @UpdateProvider(type = ModuleSqlProvider.class, method = "update")
    int update(Module record);

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

    @SelectProvider(type = ModuleSqlProvider.class, method = "query")
    @Results({
            @Result(column = "projectId", property = "projectId"),
            @Result(
                    column = "projectId", property = "project", javaType = Project.class,
                    one = @One(select = "org.grs.generator.mapper.ProjectMapper.get")
            )
    })
    List<Module> query(Module record);

    @SelectProvider(type = ModuleSqlProvider.class, method = "count")
    int count(Module record);

    class ModuleSqlProvider {
        private String queryOrCount(Module record, boolean select) {
            return new SQL() {
                {
                    SELECT(select ? "t.*" : "COUNT(*)");
                    FROM(Table + " t");
                    if (record.getName() != null) {
                        WHERE("t.name like CONCAT('%',#{name},'%')");
                    }
                    if (record.getTableName() != null) {
                        WHERE("t.tableName = #{tableName}");
                    }
                    if (record.getProjectId() != null) {
                        WHERE("projectId = #{projectId}");
                    }
                    if (select) {
                        ORDER_BY("t.id desc");
                    }
                }
            }.toString() + (select && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query(Module record) {
            return queryOrCount(record, true);
        }

        public String count(Module record) {
            return queryOrCount(record, false);
        }

        public String update(final Module record) {
            return new SQL() {
                {
                    UPDATE(Table);
                    if (record.getProjectId() != null) {
                        SET("`projectId` = #{projectId,jdbcType=INTEGER}");
                    }
                    if (record.getName() != null) {
                        SET("`name` = #{name,jdbcType=VARCHAR}");
                    }
                    if (record.getDisplayName() != null) {
                        SET("`displayName` = #{displayName,jdbcType=VARCHAR}");
                    }
                    if (record.getModelName() != null) {
                        SET("`modelName` = #{modelName,jdbcType=VARCHAR}");
                    }
                    if (record.getFullName() != null) {
                        SET("`fullName` = #{fullName,jdbcType=VARCHAR}");
                    }
                    if (record.getTableName() != null) {
                        SET("`tableName` = #{tableName,jdbcType=VARCHAR}");
                    }
                    WHERE("id = #{id,jdbcType=INTEGER}");
                }
            }.toString();
        }
    }
}