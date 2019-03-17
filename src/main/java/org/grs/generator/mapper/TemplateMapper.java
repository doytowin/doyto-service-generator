package org.grs.generator.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.model.Project;
import org.grs.generator.model.Template;

@Mapper
//@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
//@CacheNamespace(implementation = org.mybatis.caches.hazelcast.LoggingHazelcastCache.class)
public interface TemplateMapper extends IMapper<Template> {
    String Table = "gen_template";

    @Select(LIST_ + Table + _WHERE_ID)
    @Results({
            @Result(column = "projectId", property = "projectId"),
            @Result(
                    column = "projectId", property = "project", javaType = Project.class,
                    one = @One(select = "org.grs.generator.mapper.ProjectMapper.get")
            )
    })
    Template get(Serializable id);

    @Delete(DELETE_ + Table + _WHERE_ID)
    //@Options(flushCache = Options.FlushCachePolicy.FALSE)
    int delete(Serializable id);

    @Insert({
        "insert into",
        Table,
        "(`projectId`,`suffix`,`path`,`cap`,`content`)",
        "values",
        "(#{projectId},#{suffix},#{path},#{cap},#{content})"
    })
    @Options(flushCache = Options.FlushCachePolicy.FALSE)
    int insert(Template record);

    @UpdateProvider(type = TemplateSqlProvider.class, method = "update")
    @Options(flushCache = Options.FlushCachePolicy.FALSE)
    int update(Template record);

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

    @SelectProvider(type = TemplateSqlProvider.class, method = "query")
    @Results({
            @Result(
                    column = "id", property = "filler", javaType = Template.class,
                    one = @One(select = "org.grs.generator.mapper.TemplateMapper.get")
            )
    })
    @Options(useCache = false)
    List<Template> query(Template record);

    @SelectProvider(type = TemplateSqlProvider.class, method = "count")
    @Options(useCache = false)
    long count(Template record);

    class TemplateSqlProvider {
        private String queryOrCount(Template record, boolean select) {
            return new SQL() {
                {
                    SELECT(select ? "t.id" : "COUNT(*)");
                    FROM(Table + " t");
                    if (record.getProjectId() != null) {
                        WHERE("projectId = #{projectId}");
                    }
                    if (record.getValid() != null) {
                        WHERE("valid = #{valid}");
                    }
                    ORDER_BY("t.projectId");
                }
            }.toString() + (select && record.needPaging() ? _LIMIT_OFFSET : "");
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