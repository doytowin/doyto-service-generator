package org.grs.generator.service;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.grs.generator.common.AbstractService;
import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.mapper.{{gen.name | capitalize}}Mapper;
import org.grs.generator.model.{{gen.name | capitalize}};
import org.grs.generator.model.{{gen.name | capitalize}}Service;

/**
 * Default{{gen.name | capitalize}}Service
 *
 * @author f0rb on {{Date.now() | date:'yyyy-MM-dd'}}.
 */
@Slf4j
@Service
public class Default{{gen.name | capitalize}}Service extends AbstractService<{{gen.name | capitalize}}> implements {{gen.name | capitalize}}Service {

    @Resource
    private {{gen.name | capitalize}}Mapper {{gen.name}}Mapper;

    @Override
    protected IMapper<{{gen.name | capitalize}}> getIMapper() {
        return {{gen.name}}Mapper;
    }

    @Override
    public {{gen.name | capitalize}} update({{gen.name | capitalize}} {{gen.name}}) {
        {{gen.name | capitalize}} target = {{gen.name}}Mapper.get(id);
        if (target == null) {
            return null;
        }<div ng-repeat="column in columns | regex:'field':'^(?!id$|create|update)'">
        //target.set{{column.field | capitalize}}({{gen.name}}.get{{column.field | capitalize}}()); </div>
        //target.setUpdateUserId(AppContext.getLoginUserId());
        //target.setUpdateTime(new Date());
        {{gen.name}}Mapper.update(target);
        return target;
    }
}
