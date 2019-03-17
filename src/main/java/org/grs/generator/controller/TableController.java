package org.grs.generator.controller;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.grs.generator.mapper.ColumnMapper;

/**
 * TableController
 *
 * @author f0rb on 2017-01-21.
 */

@Slf4j
@RestController
@RequestMapping("/api/table")
public class TableController {

    @Resource
    private ColumnMapper columnMapper;

    /**
     * 按表名取表列
     *
     * @param table 表名
     * @return 表列
     */
    @RequestMapping(value = "{table}", method = RequestMethod.GET)
    public Object tableColumn(@PathVariable("table") String table, @RequestParam Integer projectId) {
        return columnMapper.getByTableName(projectId, table);
    }
}
