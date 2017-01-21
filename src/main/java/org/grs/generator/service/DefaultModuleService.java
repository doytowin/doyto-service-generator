package org.grs.generator.service;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.grs.generator.common.AbstractService;
import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.mapper.ColumnMapper;
import org.grs.generator.mapper.DatabaseMapper;
import org.grs.generator.mapper.ModuleMapper;
import org.grs.generator.model.Column;
import org.grs.generator.model.Module;
import org.grs.generator.model.ModuleService;

/**
 * DefaultModulService
 *
 * @author f0rb on 2017-01-21.
 */
@Slf4j
@Service
public class DefaultModuleService extends AbstractService<Module> implements ModuleService {

    private static final Pattern tablePtn = Pattern.compile("^create\\s+table\\s+(\\w+)\\s?");
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private DatabaseMapper databaseMapper;
    @Resource
    private ColumnMapper columnMapper;

    @Override
    protected IMapper<Module> getIMapper() {
        return moduleMapper;
    }

    @Override
    public Module get(Serializable id) {
        return moduleMapper.getWithProjectAndColumns(id);
    }


    @Override
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
        importModule(module.getProjectId(), module.getCreateSql());
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
        try {
            databaseMapper.createTable(createSql);

            Module module = new Module();
            module.setProjectId(projectId);
            module.setTableName(tableName);
            module.setName(tableName);
            moduleMapper.insert(module);

            //添加模块后导出表的数据库结构
            List<Column> list = databaseMapper.listColumns(tableName);
            for (Column column : list) {
                column.setTableName(tableName);
                column.setLabel(column.getField());
            }
            columnMapper.saveColumns(list);

            return module;
        } catch (Exception e) {
            throw new RuntimeException("建表出错!", e);
        } finally {
            if (tableName.length() > 0) {
                databaseMapper.dropTable(tableName);
            }
        }
    }
}
