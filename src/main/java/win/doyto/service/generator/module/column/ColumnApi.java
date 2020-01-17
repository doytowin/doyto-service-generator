package win.doyto.service.generator.module.column;

import win.doyto.query.controller.RestApi;

public interface ColumnApi extends RestApi<Integer, ColumnQuery, ColumnEntity, ColumnEntity> {
    int delete(ColumnQuery query);
}
