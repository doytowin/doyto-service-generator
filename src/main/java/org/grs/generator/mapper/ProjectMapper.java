package org.grs.generator.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.grs.generator.model.Project;

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface ProjectMapper {
    String Table = "gen_project";
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
    Project get(Serializable id);

    @Delete(DELETE_ + Table + WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
        "insert into",
        Table,
        "(`userId`,`name`,`path`)",
        "values",
        "(#{userId},#{name},#{path})"
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
    @Select(HAS + " WHERE ${column} = #{value}")
    @Options(useCache = false)
    Boolean hasValueOnColumn(@Param("column") String column, @Param("value") String value);

    @SelectProvider(type = ProjectSqlProvider.class, method = "query")
    List<Project> query(Project record);

    @SelectProvider(type = ProjectSqlProvider.class, method = "count")
    int count(Project record);

    class ProjectSqlProvider {
        private String queryOrCount(Project record, boolean select) {
            return new SQL() {
                {
                    SELECT(select ? "*" : "COUNT(*)");
                    FROM(Table);
                    if (record.getName() != null) {
                        WHERE("name like CONCAT('%',#{name},'%')");
                    }
                }
            }.toString() + (select && record.needPaging() ? LIMIT_OFFSET : "");
        }

        public String query(Project record) {
            return queryOrCount(record, true);
        }

        public String count(Project record) {
            return queryOrCount(record, false);
        }

        public String update(final Project record) {
            return new SQL() {
                {
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
                    WHERE("id = #{id,jdbcType=INTEGER}");
                }
            }.toString();
        }
    }
}