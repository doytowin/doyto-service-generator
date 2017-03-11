package org.grs.generator.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.model.Template;
import org.grs.generator.service.TemplateService;

/**
 * 模板管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
@RestController
@RequestMapping("/api/template")
public class TemplateController extends AbstractController<Template> {
    @Resource
    private TemplateService templateService;

    @Override
    IMapper<Template> getIMapper() {
        return templateService.getIMapper();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Object add(@RequestBody @Valid Template template, BindingResult result) {
        return doInsert(template, result);
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
    public Object query(Template template) {
        return doQuery(template);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public Object update(@RequestBody @Valid Template template, BindingResult result, @PathVariable("id") Integer id) {
        if (result.hasErrors()) {
            return result;
        }
        return templateService.update(template);
    }
}