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
import org.grs.generator.common.ResponseObject;
import org.grs.generator.mapper.ColumnMapper;
import org.grs.generator.mapper.DatabaseMapper;
import org.grs.generator.mapper.ModuleMapper;
import org.grs.generator.mapper.ProjectMapper;
import org.grs.generator.model.Column;
import org.grs.generator.model.Module;
import org.grs.generator.model.Project;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 模块管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
@RestController
@RequestMapping("/api/module")
public class ModuleController {
    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private DatabaseMapper databaseMapper;

    @Resource
    private ColumnMapper columnMapper;

    @Resource
    private ProjectMapper projectMapper;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseObject query(Module module) {
        ResponseObject ret = new ResponseObject();
        List moduleList = moduleMapper.query(module);
        long count = moduleMapper.count(module);
        ret.setResult(moduleList);
        ret.setTotal(count);
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseObject add(@RequestBody @Valid Module module, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        Project project = projectMapper.get(module.getProjectId());
        if (project == null) {
            ret.setMessage("项目[" + module.getProjectId() + "]不存在!");
            return ret;
        }

        String sql = module.getCreateSql();
        if (sql != null) {
            try {
                databaseMapper.createTable(sql);
            } catch (BadSqlGrammarException e) {
                ret.setMessage("建表出错:" + e.getCause().getMessage());
                return ret;
            } catch (Exception e) {
                ret.setMessage("建表出错:" + e.getMessage());
                return ret;
            }
        } else {
            ret.setMessage("建表sql不能为空");
            return ret;
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
            } catch (Exception e) {
                log.error("表[" + module.getTableName() + "]不存在", e);
            }
        }
        ret.setResult(module);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public ResponseObject update(@RequestBody @Valid Module module, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        Module target = moduleMapper.get(module.getId());
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
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
        //ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseObject get(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        Module target = moduleMapper.getWithProjectAndColumns(id);
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseObject delete(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        Module target = moduleMapper.get(id);
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        columnMapper.deleteByTableName(target.getTableName());
        moduleMapper.delete(id);
        databaseMapper.dropTable(target.getTableName());
        //ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "upload/{id}", method = RequestMethod.POST)
    public ResponseObject upload(@PathVariable("id") String id, @RequestBody JSONObject json) {
        ResponseObject ret = new ResponseObject();

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
                ret.setMessage("文件写入失败:" + e.getMessage());
            } catch (Exception e) {
                log.error("git添加失败", e);
            }
        }
        return ret;
    }

    /**
     * 按表名取表列
     *
     * @param table 表名
     * @return 表列
     */
    @RequestMapping(value = "table/{table}", method = RequestMethod.GET)
    public ResponseObject tableColumn(@PathVariable("table") String table) {
        ResponseObject ret = new ResponseObject();
        try {
            List list = databaseMapper.listColumns(table);
            ret.setResult(list);
        } catch (Exception e) {
            ret.setMessage("表[" + table + "]不存在");
        }
        return ret;
    }
}