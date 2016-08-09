package org.grs.generator.controller;

import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.grs.generator.common.ResponseObject;
import org.grs.generator.mapper.TemplateMapper;
import org.grs.generator.model.Template;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 模板管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
@RestController
@RequestMapping("/api/template")
public class TemplateController {
    @Resource
    private TemplateMapper templateMapper;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseObject query(Template template){
        ResponseObject ret = new ResponseObject();
        List templateList = templateMapper.query(template);
        long count = templateMapper.count(template);
        ret.setResult(templateList);
        ret.setTotal(count);
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseObject add(@RequestBody @Valid Template template, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        //template.setCreateUserId(AppContext.getLoginUserId());
        templateMapper.insert(template);
        ret.setResult(template);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public ResponseObject update(@RequestBody @Valid Template template, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        Template target = templateMapper.get(template.getId());
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }

        target.setSuffix(template.getSuffix());
        target.setPath(template.getPath());
        target.setCap(template.getCap());
        target.setContent(template.getContent());
        target.setValid(template.getValid());

        templateMapper.update(target);
        ret.setResult(target);
        return ret;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseObject get(@PathVariable("id") Integer id) {
        ResponseObject ret = new ResponseObject();
        Template target = templateMapper.get(id);
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
        Template target = templateMapper.get(id);
        if (target == null) {
            ret.setMessage("指定记录不存在");
            return ret;
        }
        templateMapper.delete(id);
        ret.setResult(target);
        return ret;
    }
}