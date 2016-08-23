package org.grs.generator.controller;

import java.util.*;
import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.grs.generator.common.ResponseObject;
import org.grs.generator.mapper.{{gen.name | capitalize}}Mapper;
import org.grs.generator.model.{{gen.name | capitalize}};
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * {{gen.displayName}}管理模块基本操作。
 *
 * @author Yuanzhen on {{Date.now() | date:'yyyy-MM-dd'}}.
 */
@Slf4j
@RestController
@RequestMapping("/api/{{gen.name}}")
public class {{gen.name | capitalize}}Controller {
    @Resource
    private {{gen.name | capitalize}}Mapper {{gen.name}}Mapper;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseObject query({{gen.name | capitalize}} {{gen.name}}){
        ResponseObject ret = new ResponseObject();
        List {{gen.name}}List = {{gen.name}}Mapper.query({{gen.name}});
        long count = {{gen.name}}Mapper.count({{gen.name}});
        ret.setResult({{gen.name}}List);
        ret.setTotal(count);
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseObject add(@RequestBody @Valid {{gen.name | capitalize}} {{gen.name}}, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        //{{gen.name}}.setCreateUserId(AppContext.getLoginUserId());
        {{gen.name}}Mapper.insert({{gen.name}});
        ret.setResult({{gen.name}});
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public ResponseObject update(@RequestBody @Valid {{gen.name | capitalize}} {{gen.name}}, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        {{gen.name | capitalize}} target = {{gen.name}}Mapper.get({{gen.name}}.getId());
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
<div ng-repeat="column in columns | regex:'field':'^(?!id$|create|update)'">        //target.set{{column.field | capitalize}}({{gen.name}}.get{{column.field | capitalize}}());
</div>
        //target.setUpdateUserId(AppContext.getLoginUserId());
        //target.setUpdateTime(new Date());
        {{gen.name}}Mapper.update(target);
        ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseObject get(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        {{gen.name | capitalize}} target = {{gen.name}}Mapper.get(id);
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
        {{gen.name | capitalize}} target = {{gen.name}}Mapper.get(id);
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        {{gen.name}}Mapper.delete(id);
        ret.setResult(target);
        return ret;
    }
}