package org.grs.generator.component.mybatis;

import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

/**
 * CachingExecutorInterceptor
 *
 * @author f0rb on 2017-01-16.
 */
@Slf4j
@Intercepts(@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}))
public class CachingExecutorInterceptor implements Interceptor {
    public Object removeCacheEntry(Configuration configuration, String cacheId, String mappingName, Object parameterObject) {
        Object removedObject = null;
        Cache cache = configuration.getCache(cacheId);;
        if (cache != null) {
            CacheKey cacheKey = getCacheKey(configuration, mappingName, parameterObject);
            if (cacheKey != null) {
                removedObject = cache.removeObject(cacheKey);
                log.info("Remove from cache: {} by key {}", removedObject, cacheKey);
            }
        }
        return removedObject;
    }

    private CacheKey getCacheKey(Configuration configuration, String mappingName, Object parameterObject) {
        SimpleExecutor executor = new SimpleExecutor(configuration, null);
        MappedStatement mappedStatement = configuration.getMappedStatement(mappingName);
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        return executor.createCacheKey(mappedStatement, parameterObject, RowBounds.DEFAULT, boundSql);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object ret = invocation.proceed();

        if (invocation.getTarget() instanceof CachingExecutor) {
            //执行完update后清理对应Object的缓存
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            String msId = ms.getId();
            Integer lastDotIndex = msId.lastIndexOf('.');
            String methodName = msId.substring(lastDotIndex + 1);

            if (methodName.equals("update") || methodName.equals("insert") || methodName.equals("delete")) {
                String className = msId.substring(0, lastDotIndex);
                String mappingName = className + ".get";
                removeCacheEntry(ms.getConfiguration(), className, mappingName, invocation.getArgs()[1]);
            }
        }
        return ret;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties arg0) {
        log.info(arg0.toString());
    }
}
