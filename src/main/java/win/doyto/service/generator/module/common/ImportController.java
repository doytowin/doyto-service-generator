package win.doyto.service.generator.module.common;

import com.zaxxer.hikari.util.DriverDataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import win.doyto.query.web.response.ErrorCode;
import win.doyto.query.web.response.ErrorCodeException;
import win.doyto.service.generator.module.column.ColumnApi;
import win.doyto.service.generator.module.column.ColumnEntity;
import win.doyto.service.generator.module.module.ModuleApi;
import win.doyto.service.generator.module.module.ModuleEntity;
import win.doyto.service.generator.module.module.ModuleQuery;
import win.doyto.service.generator.module.module.ModuleRequest;
import win.doyto.service.generator.module.project.ProjectApi;
import win.doyto.service.generator.module.project.ProjectEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;

/**
 * DefaultModuleService
 *
 * @author f0rb on 2017-01-21.
 */
@Slf4j
@RestController
@RequestMapping("/api/import")
@AllArgsConstructor
public class ImportController {

    private static final Pattern tablePtn = Pattern.compile("^create\\s+table\\s+`?(\\w+)`?\\s?");

    private ProjectApi projectApi;

    private ModuleApi moduleApi;
    private ColumnApi columnApi;

    public static String camelize(String str) {
        String[] arr = str.split("_");
        for (int i = 1; i < arr.length; i++) {
            arr[i] = StringUtils.capitalize(arr[i]);
        }
        return StringUtils.join(arr);
    }

    @PostMapping("project")
    @Transactional
    @SuppressWarnings("java:S125")
    public void importDatabase(Integer projectId) {
        // 1.建立数据库连接
        // 2.SHOW TABLES;
        // 3.SHOW CREATE TABLE xxx;
        ProjectEntity projectEntity = projectApi.get(projectId);

        if (DbUtils.loadDriver(projectEntity.getJdbcDriver())) {
            try {
                QueryRunner run = new QueryRunner();
                Connection conn = DriverManager.getConnection(projectEntity.getJdbcUrl(), projectEntity.getJdbcUsername(), projectEntity.getJdbcPassword());
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
                        importModule(projectId, tableSqlEntry.getKey(), tableSqlEntry.getValue());
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

    @PostMapping("module")
    @Transactional
    public void importModule(@RequestBody ModuleRequest moduleRequest) {
        importModule(moduleRequest.getProjectId(), moduleRequest.getCreateSql());
    }

    public void importModule(Integer projectId, String createSql) {
        Matcher matcher = tablePtn.matcher(createSql.toLowerCase());
        String tableName = "";
        if (matcher.find()) {
            tableName = matcher.group(1);
        }
        importModule(projectId, tableName, createSql);
    }

    private void importModule(Integer projectId, String tableName, String createSql) {
        importModule(projectApi.get(projectId), tableName, createSql);
    }

    private void importModule(ProjectEntity projectEntity, String tableName, String createSql) {
        DataSource dataSource = null;
        try {
            dataSource = getDataSource(projectEntity);
            QueryRunner queryRunner = new QueryRunner(dataSource);
            testTable(tableName, createSql, queryRunner);

            Integer projectId = projectEntity.getId();
            List<ModuleEntity> moduleEntityList = moduleApi.query(ModuleQuery.builder().projectId(projectId).tableName(tableName).build());

            ModuleEntity moduleEntity;
            if (!moduleEntityList.isEmpty()) {
                log.warn("Model已存在: projectId={}, tableName={}", projectId, tableName);
            } else {
                moduleEntity = new ModuleEntity();
                moduleEntity.setProjectId(projectId);
                moduleEntity.setTableName(tableName);
                moduleEntity.setName(camelize(StringUtils.removeStart(tableName, projectEntity.getTablePrefix())));
                String capitalizeName = StringUtils.capitalize(moduleEntity.getName());
                moduleEntity.setDisplayName(capitalizeName);
                moduleEntity.setModelName(capitalizeName);
                moduleEntity.setFullName(capitalizeName);
                moduleApi.create(moduleEntity);
            }

            //添加模块后导出表的数据库结构
            List<ColumnEntity> list = queryRunner.query(
                    "show full columns from `" + tableName + "`",
                    new BeanListHandler<>(ColumnEntity.class, new BasicRowProcessor(new GenerousBeanProcessor())));
            for (ColumnEntity columnEntity : list) {
                columnEntity.setTableName(tableName);
                columnEntity.setLabel(columnEntity.getField());
                columnEntity.setProjectId(projectId);
            }
            columnApi.create(list);

        } catch (Exception e) {
            log.error("建表出错!", e);
            throw new ErrorCodeException(ErrorCode.build("建表出错:" + e.getMessage()));
        } finally {
            if (dataSource != null) {
                try {
                    dataSource.getConnection().close();
                } catch (SQLException e) {//ignore
                }
            }
        }
    }

    private void testTable(String tableName, String createSql, QueryRunner queryRunner) throws SQLException {
        try {
            queryRunner.query("DESC `" + tableName + "`", new BeanListHandler<>(ColumnEntity.class));
        } catch (SQLException e) {
            queryRunner.update(createSql);
        }
    }

    private DataSource getDataSource(ProjectEntity projectEntity) {
        return new DriverDataSource(
                projectEntity.getJdbcUrl(),
                projectEntity.getJdbcDriver(),
                new Properties(),
                projectEntity.getJdbcUsername(),
                projectEntity.getJdbcPassword());
    }

}
