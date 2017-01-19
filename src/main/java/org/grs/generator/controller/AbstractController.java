package org.grs.generator.controller;

import java.io.Serializable;
import java.util.List;

import org.springframework.validation.BindingResult;

import org.grs.generator.common.Pageable;
import org.grs.generator.component.mybatis.IMapper;

/**
 * AbstractController
 *
 * @author f0rb on 2017-01-17.
 */
abstract class AbstractController<T extends Pageable<T>> {
    final Object EMPTY = new Object();

    IMapper<T> getIMapper() {
        return null;
    }

    //Object doInsert(IMapper<T> mapper, T insert, BindingResult result) {
    //    if (result.hasErrors()) {
    //        return result;
    //    }
    //    mapper.insert(insert);
    //    return insert;
    //}
    //
    //Object doDelete(IMapper<T> mapper, Serializable id) {
    //    T target = mapper.get(id);
    //    if (target == null) {
    //        return null;
    //    }
    //    int count = mapper.delete(id);
    //    return count > 0 ? id : null;
    //}
    //
    //Object doQuery(IMapper<T> mapper, T query) {
    //    Pageable.Page page = new Pageable.Page();
    //    List columnList = mapper.query(query);
    //    long total = mapper.count(query);
    //    page.setData(columnList);
    //    page.setTotal(total);
    //    return page;
    //}
    //
    //Object doGet(IMapper<T> mapper, Serializable id) {
    //    return mapper.get(id);
    //}

    Object doInsert(T insert, BindingResult result) {
        if (result.hasErrors()) {
            return result;
        }
        getIMapper().insert(insert);
        return insert;
    }

    Object doDelete(Serializable id) {
        T target = getIMapper().get(id);
        if (target == null) {
            return null;
        }
        int count = getIMapper().delete(id);
        return count > 0 ? id : null;
    }

    Object doQuery(T query) {
        Pageable.Page page = new Pageable.Page();
        List columnList = getIMapper().query(query);
        long total = getIMapper().count(query);
        page.setData(columnList);
        page.setTotal(total);
        return page;
    }

    Object doGet(Serializable id) {
        return getIMapper().get(id);
    }

}
