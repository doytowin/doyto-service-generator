package org.grs.generator.controller;

import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.grs.generator.common.ResponseObject;
import org.grs.generator.mapper.ProjectMapper;
import org.grs.generator.model.Project;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 项目管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
@RestController
@RequestMapping("/api/project")
public class ProjectController {
    @Resource
    private ProjectMapper projectMapper;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseObject query(Project project){
        ResponseObject ret = new ResponseObject();
        List projectList = projectMapper.query(project);
        long count = projectMapper.count(project);
        ret.setResult(projectList);
        ret.setTotal(count);
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseObject add(@RequestBody @Valid Project project, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        projectMapper.insert(project);
        ret.setResult(project);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public ResponseObject update(@RequestBody @Valid Project project, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        Project target = projectMapper.get(project.getId());
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        target.setName(project.getName());
        target.setPath(project.getPath());

        projectMapper.update(target);
        ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseObject get(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        Project target = projectMapper.get(id);
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseObject delete(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        Project target = projectMapper.get(id);
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        projectMapper.delete(id);
        ret.setResult(target);
        return ret;
    }
}