package win.doyto.service.generator.module.column;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import win.doyto.query.web.controller.AbstractIQEEController;

import java.util.List;
import javax.validation.Valid;

/**
 * 表列管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-23.
 */
@Slf4j
@RestController
@RequestMapping("/api/column")
public class ColumnController extends AbstractIQEEController<ColumnEntity, Integer, ColumnQuery> implements ColumnApi {

    @PostMapping("batch")
    public void batch(@RequestBody @Valid List<ColumnEntity> columnEntities) {
        create(columnEntities, "label");
    }
}