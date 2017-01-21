package org.grs.generator.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.grs.generator.mapper.{{gen.name | capitalize}}Mapper;
import org.grs.generator.model.{{gen.name | capitalize}};

/**
 * {{gen.displayName}}管理模块基本操作。
 *
 * @author Yuanzhen on {{Date.now() | date:'yyyy-MM-dd'}}.
 */
@Slf4j
@RestController
@RequestMapping("/api/{{gen.name}}")
public class {{gen.name | capitalize}}Controller extends AbstractController<{{gen.name | capitalize}}> {
    @Resource
    private {{gen.name | capitalize}}Mapper {{gen.name}}Mapper;

    @Override
    {{gen.name | capitalize}}Mapper getIMapper() {
        return {{gen.name}}Mapper;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Object add(@RequestBody @Valid {{gen.name | capitalize}} {{gen.name}}, BindingResult result) {
        return doInsert({{gen.name}}, result);
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
    public Object query({{gen.name | capitalize}} {{gen.name}}) {
        return doQuery({{gen.name}});
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public Object update(@RequestBody @Valid {{gen.name | capitalize}} {{gen.name}}, BindingResult result, @PathVariable("id") Integer id) {
        if (result.hasErrors()) {
            return result;
        }
        {{gen.name | capitalize}} target = {{gen.name}}Mapper.get(id);
        if (target == null) {
            return null;
        }<div ng-repeat="column in columns | regex:'field':'^(?!id$|create|update)'">
        //target.set{{column.field | capitalize}}({{gen.name}}.get{{column.field | capitalize}}()); </div>
        //target.setUpdateUserId(AppContext.getLoginUserId());
        //target.setUpdateTime(new Date());
        {{gen.name}}Mapper.update(target);
        return target;
    }
}