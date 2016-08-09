package org.grs.generator.controller;

import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.grs.generator.common.ResponseObject;
import org.grs.generator.mapper.ColumnMapper;
import org.grs.generator.model.Column;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 表字段管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-23.
 */
@Slf4j
@RestController
@RequestMapping("/api/column")
public class ColumnController {
    @Resource
    private ColumnMapper columnMapper;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseObject saveLabels(@RequestBody @Valid List<Column> columns, BindingResult result) {
        ResponseObject ret = new ResponseObject();
        if (result.hasErrors()) {
            ret.setMessage(result.getFieldError().getDefaultMessage());
            return ret;
        }
        columnMapper.saveColumns(columns);
        return ret;
    }
}