package org.grs.generator.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.grs.generator.mapper.ProjectMapper;
import org.grs.generator.model.Project;

/**
 * 项目管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
@RestController
@RequestMapping("/api/project")
public class ProjectController extends AbstractController<Project> {
    @Resource
    private ProjectMapper projectMapper;

    @Override
    ProjectMapper getIMapper() {
        return projectMapper;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Object add(@RequestBody @Valid Project project, BindingResult result) {
        return doInsert(project, result);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable("id") Integer id) {
        return doDelete(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Object get(@PathVariable("id") Integer id) {
        return doGet(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Object query(Project project) {
        return doQuery(project);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public Object update(@RequestBody @Valid Project project, BindingResult result, @PathVariable("id") Integer id) {
        if (result.hasErrors()) {
            return result;
        }
        Project target = projectMapper.get(id);
        if (target == null) {
            return null;
        }
        target.setName(project.getName());
        target.setPath(project.getPath());

        projectMapper.update(target);
        return target;
    }
}