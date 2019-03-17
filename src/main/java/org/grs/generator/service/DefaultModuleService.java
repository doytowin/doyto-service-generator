package org.grs.generator.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.sql.DataSource;

import com.zaxxer.hikari.util.DriverDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.grs.generator.common.AbstractService;
import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.mapper.ColumnMapper;
import org.grs.generator.mapper.ModuleMapper;
import org.grs.generator.mapper.ProjectMapper;
import org.grs.generator.model.Column;
import org.grs.generator.model.Module;
import org.grs.generator.model.Project;
import org.grs.generator.util.StringUtil;

/**
 * DefaultModuleService
 *
 * @author f0rb on 2017-01-21.
 */
@Slf4j
@Service
public class DefaultModuleService extends AbstractService<Module> implements ModuleService {

    private static final Pattern tablePtn = Pattern.compile("^create\\s+table\\s+`?(\\w+)`?\\s?");
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private ColumnMapper columnMapper;

    @Override
    public IMapper<Module> getIMapper() {
        return moduleMapper;
    }

    @Override
    public Module get(Serializable id) {
        return moduleMapper.getWithProjectAndColumns(id);
    }

    @Override
    @Transactional
    public Module delete(Serializable id) {
        Module target = moduleMapper.get(id);
        if (target == null) {
            return null;
        }
        columnMapper.deleteByTableName(target.getTableName());
        moduleMapper.delete(id);
        return target;
    }

    @Override
    public Module update(Module module) {
        Module target = moduleMapper.get(module.getId());
        if (target == null) {
            return null;
        }
        //target.setProjectId(module.getProjectId());
        target.setName(module.getName());
        target.setDisplayName(module.getDisplayName());
        target.setModelName(module.getModelName());
        target.setFullName(module.getFullName());
        //target.setTableName(module.getTableName());

        moduleMapper.update(target);
        return target;
    }

    @Override
    public Module importModule(Module module) {
        Module newModule = importModule(module.getProjectId(), module.getCreateSql());
        module.setId(newModule.getId());
        update(module);
        return module;
    }

    @Override
    @Transactional
    public Module importModule(Integer projectId, String createSql) {
        Matcher matcher = tablePtn.matcher(createSql.toLowerCase());
        String tableName = "";
        if (matcher.find()) {
            tableName = matcher.group(1);
        }
        return importModule(projectId, tableName, createSql);
    }

    @Override
    @Transactional
    public Module importModule(Integer projectId, String tableName, String createSql) {
        return importModule(projectMapper.get(projectId), tableName, createSql);
    }

    @Override
    @Transactional
    public Module importModule(Project project, String tableName, String createSql) {
        DataSource dataSource = null;
        try {
            Integer projectId = project.getId();
            dataSource = getDataSource(project);

            QueryRunner queryRunner = new QueryRunner(dataSource);
            Module query = new Module();
            query.setProjectId(projectId);
            query.setTableName(tableName);
            List<Module> moduleList = moduleMapper.query(query);

            //createTable(conn, createSql);
            try {
                queryRunner.query("DESC `" + tableName + "`", new BeanListHandler<>(Column.class));
            } catch (SQLException e ){
                queryRunner.update(createSql);
            }

            Module module;
            if (!moduleList.isEmpty()) {
                log.warn("Model已存在: projectId={}, tableName={}", projectId, tableName);
                module = moduleList.get(0);
            } else {
                module = new Module();
                module.setProjectId(projectId);
                module.setTableName(tableName);
                module.setName(StringUtil.camelize(StringUtils.removeStart(tableName, project.getTablePrefix())));
                String capitalizeName = StringUtil.capitalize(module.getName());
                module.setDisplayName(capitalizeName);
                module.setModelName(capitalizeName);
                module.setFullName(capitalizeName);
                moduleMapper.insert(module);
            }

            //添加模块后导出表的数据库结构
            //List<Column> list = listColumns(conn, tableName);
            List<Column> list = queryRunner.query(
                    "show full columns from `" + tableName + "`",
                    new BeanListHandler<>(Column.class, new BasicRowProcessor(new GenerousBeanProcessor())));
            for (Column column : list) {
                column.setTableName(tableName);
                column.setLabel(column.getField());
                column.setProjectId(projectId);
            }
            columnMapper.saveColumns(list);

            return module;
        } catch (Exception e) {
            throw new RuntimeException("建表出错!", e);
        } finally {
            if (dataSource != null) {
                try {
                    dataSource.getConnection().close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private DataSource getDataSource(Project project) throws SQLException {
        return new DriverDataSource(project.getJdbcUrl(), project.getJdbcDriver(), new Properties(), project.getJdbcUsername(), project.getJdbcPassword());
    }

}
