package org.grs.generator.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.model.Project;

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface ProjectMapper extends IMapper<Project> {
    String Table = "gen_project";

    @Select(LIST_ + Table + _WHERE_ID)
    Project get(Serializable id);

    @Delete(DELETE_ + Table + _WHERE_ID)
    int delete(Serializable id);

    @Insert({
            "insert into",
            Table,
            "(`userId`,`name`,`path`,`jdbcDriver`,`jdbcUrl`,`jdbcUsername`,`jdbcPassword`)",
            "values",
            "(#{userId},#{name},#{path},#{jdbcDriver},#{jdbcUrl},#{jdbcUsername},#{jdbcPassword})"
    })
    int insert(Project record);

    @UpdateProvider(type = ProjectSqlProvider.class, method = "update")
    int update(Project record);

    /**
     * 检查某列是否存在某值
     *
     * @param column 列名
     * @param value  待检值
     * @return 如果值存在, 则返回true; 否则返回false
     */
    @Select(HAS_ + Table + " WHERE ${column} = #{value}")
    @Options(useCache = false)
    Boolean hasValueOnColumn(@Param("column") String column, @Param("value") String value);

    @SelectProvider(type = ProjectSqlProvider.class, method = "query")
    List<Project> query(Project record);

    @SelectProvider(type = ProjectSqlProvider.class, method = "count")
    long count(Project record);

    class ProjectSqlProvider {
        private String queryOrCount(Project record, boolean query) {
            return new SQL() {{
                SELECT(query ? "*" : "COUNT(*)");
                FROM(Table);
                if (record.getName() != null) {
                    WHERE("name like CONCAT(#{name},'%')");
                }
            }}.toString() + (query && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query(Project record) {
            return queryOrCount(record, true);
        }

        public String count(Project record) {
            return queryOrCount(record, false);
        }

        public String update(final Project record) {
            return new SQL() {{
                UPDATE(Table);
                if (record.getUserId() != null) {
                    SET("`userId` = #{userId,jdbcType=INTEGER}");
                }
                if (record.getName() != null) {
                    SET("`name` = #{name,jdbcType=VARCHAR}");
                }
                if (record.getPath() != null) {
                    SET("`path` = #{path,jdbcType=VARCHAR}");
                }
                if (record.getJdbcDriver() != null) {
                    SET("`jdbcDriver` = #{jdbcDriver,jdbcType=VARCHAR}");
                }
                if (record.getJdbcUrl() != null) {
                    SET("`jdbcUrl` = #{jdbcUrl,jdbcType=VARCHAR}");
                }
                if (record.getJdbcUsername() != null) {
                    SET("`jdbcUsername` = #{jdbcUsername,jdbcType=VARCHAR}");
                }
                if (record.getJdbcPassword() != null) {
                    SET("`jdbcPassword` = #{jdbcPassword,jdbcType=VARCHAR}");
                }
                WHERE("id = #{id,jdbcType=INTEGER}");
            }}.toString();
        }
    }
}