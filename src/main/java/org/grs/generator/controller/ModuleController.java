package org.grs.generator.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.grs.generator.mapper.ColumnMapper;
import org.grs.generator.mapper.DatabaseMapper;
import org.grs.generator.mapper.ModuleMapper;
import org.grs.generator.mapper.ProjectMapper;
import org.grs.generator.model.Column;
import org.grs.generator.model.Module;
import org.grs.generator.model.Project;

/**
 * 模块管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
@RestController
@RequestMapping("/api/module")
public class ModuleController extends AbstractController<Module> {
    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private DatabaseMapper databaseMapper;

    @Resource
    private ColumnMapper columnMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Override
    ModuleMapper getIMapper() {
        return moduleMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Object query(Module module) {
        return doQuery(module);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public Object add(@RequestBody @Valid Module module, BindingResult result) {
        if (result.hasErrors()) {
            return result;
        }
        Project project = projectMapper.get(module.getProjectId());
        if (project == null) {
            throw new RuntimeException("项目[" + module.getProjectId() + "]不存在!");
        }

        String sql = module.getCreateSql();
        try {
            databaseMapper.createTable(sql);
        } catch (Exception e) {
            throw new RuntimeException("建表出错!", e);
        }

        //module.setCreateUserId(AppContext.getLoginUserId());
        moduleMapper.insert(module);

        //添加模块后导出表的数据库结构
        String tableName = module.getTableName();
        if (StringUtils.isNotEmpty(tableName)) {
            try {
                List<Column> list = databaseMapper.listColumns(tableName);
                for (Column column : list) {
                    column.setTableName(tableName);
                    column.setLabel(column.getField());
                }
                columnMapper.saveColumns(list);
                databaseMapper.dropTable(tableName);
            } catch (Exception e) {
                log.error("表[" + module.getTableName() + "]不存在", e);
            }
        }
        return module;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public Object update(@RequestBody @Valid Module module, BindingResult result) {
        if (result.hasErrors()) {
            return result;
        }
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

        //target.setUpdateUserId(AppContext.getLoginUserId());
        //target.setUpdateTime(new Date());
        moduleMapper.update(target);
        return target;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Object get(@PathVariable("id") Integer id) {
        return moduleMapper.getWithProjectAndColumns(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @Transactional
    public Object delete(@PathVariable("id") Integer id) {
        Module target = moduleMapper.get(id);
        if (target == null) {
            return null;
        }
        columnMapper.deleteByTableName(target.getTableName());
        moduleMapper.delete(id);
        //databaseMapper.dropTable(target.getTableName());
        return id;
    }

    @RequestMapping(value = "upload/{id}", method = RequestMethod.POST)
    public Object upload(@PathVariable("id") String id, @RequestBody JSONObject json) {

        String rootPath = json.getString("root");
        JSONArray jsonArray = json.getJSONArray("data");

        if (StringUtils.isEmpty(rootPath)) {
            rootPath = "";//AppContext.getContextRealPath("res" + File.separator + "tmp" + File.separator);
        }
        Runtime run = Runtime.getRuntime();
        File dir = new File(rootPath);
        for (int i = 0, len = jsonArray.size(); i < len; i++) {
            JSONObject data = jsonArray.getJSONObject(i);
            File file = new File(rootPath + File.separator + data.getString("path"));
            try {
                FileUtils.write(file, data.getString("text"), StandardCharsets.UTF_8);
                run.exec("git -c core.quotepath=false add -- " + data.getString("path"), null, dir);
            } catch (IOException e) {
                log.error("文件写入失败", e);
                throw new RuntimeException("文件写入失败!", e);
            } catch (Exception e) {
                log.error("git添加失败", e);
            }
        }
        return EMPTY;
    }
}