package win.doyto.service.generator.module.column;

import win.doyto.query.service.CrudService;

public interface ColumnApi extends CrudService<ColumnEntity, Integer, ColumnQuery> {
    int delete(ColumnQuery query);
}
