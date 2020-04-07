package win.doyto.service.generator.module.column;

import win.doyto.query.web.controller.RestApi;

public interface ColumnApi extends RestApi<Integer, ColumnQuery, ColumnEntity, ColumnEntity> {
    int delete(ColumnQuery query);
}
