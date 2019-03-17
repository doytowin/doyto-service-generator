package org.grs.generator.common;

import java.io.Serializable;
import java.util.List;

/**
 * AbstractService
 *
 * @author f0rb on 2017-01-21.
 */
public abstract class AbstractService<T> implements IService<T> {

    @Override
    public T get(Serializable id) {
        return getIMapper().get(id);
    }

    @Override
    public T delete(Serializable id) {
        T target = getIMapper().get(id);
        if (target == null) {
            return null;
        }
        int count = getIMapper().delete(id);
        return count > 0 ? target : null;
    }

    @Override
    public T insert(T insert) {
        getIMapper().insert(insert);
        return insert;
    }

    @Override
    public PageResponse<T> query(T query) {
        List<T> columnList = getIMapper().query(query);
        long total = getIMapper().count(query);
        return new PageResponse<T>(columnList, total);
    }
}
