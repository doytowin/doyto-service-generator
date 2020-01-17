package win.doyto.service.generator.module.column;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import win.doyto.query.entity.EntityAspect;
import win.doyto.service.generator.module.module.ModuleEntity;

import javax.annotation.Resource;

/**
 * ModuleEntityAspect
 *
 * @author f0rb
 * @date 2019-10-30
 */
@Component
public class ModuleEntityAspect implements EntityAspect<ModuleEntity> {

    @Resource
    private ColumnApi columnApi;

    @Override
    public void afterDelete(ModuleEntity moduleEntity) {
        String tableName = moduleEntity.getTableName();
        if (StringUtils.isNotEmpty(tableName)) {
            ColumnQuery query = ColumnQuery.builder().projectId(moduleEntity.getProjectId()).tableName(tableName).build();
            columnApi.delete(query);
        }
    }
}
