package org.grs.generator.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.annotation.Resource;
import javax.validation.Valid;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.model.Module;
import org.grs.generator.service.ModuleService;
import org.grs.generator.model.Project;
import org.grs.generator.service.ProjectService;

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
    private ModuleService moduleService;

    @Resource
    private ProjectService projectService;

    @Override
    IMapper<Module> getIMapper() {
        return moduleService.getIMapper();
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
        Project project = projectService.get(module.getProjectId());
        if (project == null) {
            FieldError fieldError = new FieldError("Module", "projectId","项目[" + module.getProjectId() + "]不存在!");
            result.addError(fieldError);
            return result;
        }

        return moduleService.importModule(module);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public Object update(@RequestBody @Valid Module module, BindingResult result) {
        if (result.hasErrors()) {
            return result;
        }
        return moduleService.update(module);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Object get(@PathVariable("id") Integer id) {
        return moduleService.get(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @Transactional
    public Object delete(@PathVariable("id") Integer id) {
        Module deleted = moduleService.delete(id);
        return deleted != null ? id : null;
    }

    @RequestMapping(value = "upload/{id}", method = RequestMethod.POST)
    public Object upload(@PathVariable("id") String id, @RequestBody JSONObject json) {

        String rootPath = json.getString("root");
        JSONArray jsonArray = json.getJSONArray("data");

        if (StringUtils.isEmpty(rootPath)) {
            rootPath = "";
        }
        Runtime run = Runtime.getRuntime();
        File dir = new File(rootPath);
        for (int i = 0, len = jsonArray.size(); i < len; i++) {
            JSONObject data = jsonArray.getJSONObject(i);
            File file = new File(rootPath + File.separator + data.getString("path"));
            try {
                FileUtils.write(file, data.getString("text"), StandardCharsets.UTF_8);
                Process  process = run.exec("git -c core.quotepath=false add -- " + file.getName(), null, file.getParentFile());
                log.info("git add result: ", process.waitFor());
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