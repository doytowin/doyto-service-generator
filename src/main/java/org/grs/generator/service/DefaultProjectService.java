package org.grs.generator.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.stereotype.Service;

import org.grs.generator.common.AbstractService;
import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.mapper.ProjectMapper;
import org.grs.generator.model.Project;

/**
 * DefaultModulService
 *
 * @author f0rb on 2017-01-21.
 */
@Slf4j
@Service
public class DefaultProjectService extends AbstractService<Project> implements ProjectService {

    private static final Pattern tablePtn = Pattern.compile("^create\\s+table\\s+(\\w+)\\s?");
    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ModuleService moduleService;

    @Override
    public IMapper<Project> getIMapper() {
        return projectMapper;
    }

    @Override
    public Project update(Project project) {
        Project target = projectMapper.get(project.getId());
        if (target == null) {
            return null;
        }
        //target.setUserId(project.getUserId());
        target.setName(project.getName());
        target.setPath(project.getPath());
        target.setJdbcDriver(project.getJdbcDriver());
        target.setJdbcUrl(project.getJdbcUrl());
        target.setJdbcUsername(project.getJdbcUsername());
        target.setJdbcPassword(project.getJdbcPassword());

        projectMapper.update(target);
        return target;
    }

    @Override
    public void importDatabase(Integer projectId) {
        //建立数据库连接
        //SHOW TABLES ;
        //SHOW CREATE TABLE Nile;
        Project project = projectMapper.get(projectId);

        if (DbUtils.loadDriver(project.getJdbcDriver())) {
            try {
                QueryRunner run = new QueryRunner();
                Connection conn = DriverManager.getConnection(project.getJdbcUrl(), project.getJdbcUsername(), project.getJdbcPassword());
                try {
                    //查询数据库的所有表
                    List<String> tables = run.query(conn, "SHOW TABLES", resultSet -> {
                        List<String> list = new LinkedList<>();
                        while (resultSet.next()) {
                            list.add(resultSet.getString(1));
                        }
                        return list;
                    });
                    //查询数据库的所有表对应的建表SQL
                    Map<String, String> tableSqlMap = new HashMap<>();
                    for (String table : tables) {
                        run.query(conn, "SHOW CREATE TABLE `" + table + "`", resultSet -> {
                            if (resultSet.next()) {
                                tableSqlMap.put(table, resultSet.getString(2));
                            }
                            return null;
                        });
                    }

                    for (Map.Entry<String, String> tableSqlEntry : tableSqlMap.entrySet()) {
                        log.info("导入表结构: {} - {} ", tableSqlEntry.getKey(), tableSqlEntry.getValue());
                        moduleService.importModule(projectId, tableSqlEntry.getKey(), tableSqlEntry.getValue());
                    }
                } finally {
                    // Use this helper method so we don't have to check for null
                    DbUtils.close(conn);
                }
            } catch (SQLException e) {
                log.error("建表出错!", e);
            }
        }
    }
}
