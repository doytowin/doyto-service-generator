package org.grs.generator.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.grs.generator.model.Project;
import org.grs.generator.model.Template;

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface TemplateMapper {
    String Table = "gen_template";
    String ASC = " ORDER BY id ASC";
    String DESC = " ORDER BY id DESC";
    String ORDER_BY_ID = " ORDER BY id ${orderBy}";
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

    String LIST = LIST_ + Table;
    String HAS = HAS_ + Table;

    @Select(LIST + WHERE_ID)
    Template get(Serializable id);

    @Delete(DELETE_ + Table + WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
        "insert into",
        Table,
        "(`projectId`,`suffix`,`path`,`cap`,`content`)",
        "values",
        "(#{projectId},#{suffix},#{path},#{cap},#{content})"
    })
    int insert(Template record);

    @UpdateProvider(type = TemplateSqlProvider.class, method = "update")
    int update(Template record);

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

    @SelectProvider(type = TemplateSqlProvider.class, method = "query")
    @Results({
            @Result(column = "projectId", property = "projectId"),
            @Result(
                    column = "projectId", property = "project", javaType = Project.class,
                    one = @One(select = "org.grs.generator.mapper.ProjectMapper.get")
            )
    })
    List<Template> query(Template record);

    @SelectProvider(type = TemplateSqlProvider.class, method = "count")
    int count(Template record);

    class TemplateSqlProvider {
        private String queryOrCount(Template record, boolean select) {
            return new SQL() {
                {
                    SELECT(select ? "t.*" : "COUNT(*)");
                    FROM(Table + " t");
                    if (record.getProjectId() != null) {
                        WHERE("projectId = #{projectId}");
                    }
                    ORDER_BY("t.projectId");
                }
            }.toString() + (select && record.needPaging() ? LIMIT_OFFSET : "");
        }

        public String query(Template record) {
            return queryOrCount(record, true);
        }

        public String count(Template record) {
            return queryOrCount(record, false);
        }

        public String update(final Template record) {
            return new SQL() {
                {
                    UPDATE(Table);
                    if (record.getProjectId() != null) {
                        SET("`projectId` = #{projectId,jdbcType=INTEGER}");
                    }
                    if (record.getSuffix() != null) {
                        SET("`suffix` = #{suffix,jdbcType=VARCHAR}");
                    }
                    if (record.getPath() != null) {
                        SET("`path` = #{path,jdbcType=VARCHAR}");
                    }
                    if (record.getCap() != null) {
                        SET("`cap` = #{cap,jdbcType=BIT}");
                    }
                    if (record.getContent() != null) {
                        SET("`content` = #{content,jdbcType=VARCHAR}");
                    }
                    if (record.getValid() != null) {
                        SET("`valid` = #{valid,jdbcType=BIT}");
                    }
                    WHERE("id = #{id,jdbcType=INTEGER}");
                }
            }.toString();
        }
    }
}