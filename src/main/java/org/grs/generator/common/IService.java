package org.grs.generator.common;

import java.io.Serializable;

/**
 * ModuleService
 *
 * @author f0rb on 2017-01-21.
 */
public interface IService<T> {

    T insert(T record);

    T delete(Serializable id);

    T get(Serializable id);

    T update(T record);

    PageResponse<T> query(T query);

}