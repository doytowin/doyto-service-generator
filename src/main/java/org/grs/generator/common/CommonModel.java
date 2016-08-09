package org.grs.generator.common;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * Class AbstractModel ...
 *
 * @author f0rb
 * @version 1.0.0 2011-1-2
 */
public abstract class CommonModel<T extends CommonModel<?>> implements Cloneable, Serializable {

    /**
     * 返回一个装填了内部数据的model对象
     * <p/>
     * 默认返回用初始化数据填充的model
     *
     * @return T 待插入数据库的model对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
