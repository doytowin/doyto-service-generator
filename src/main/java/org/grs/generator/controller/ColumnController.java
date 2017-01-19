package org.grs.generator.controller;

import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.mapper.ColumnMapper;
import org.grs.generator.model.Column;

/**
 * 表字段管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-23.
 */
@Slf4j
@RestController
@RequestMapping("/api/column")
public class ColumnController extends AbstractController<Column> {
    @Resource
    private ColumnMapper columnMapper;

    @Override
    IMapper<Column> getIMapper() {
        return columnMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Object query(Column column) {
        return doQuery(column);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Object saveLabels(@RequestBody @Valid List<Column> columns, BindingResult result) {
        if (result.hasErrors()) {
            return result;
        }
        return columnMapper.saveColumns(columns);
    }
}