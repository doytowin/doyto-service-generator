package win.doyto.service.generator.module.module;

import lombok.Getter;
import lombok.Setter;
import win.doyto.service.generator.module.column.ColumnEntity;
import win.doyto.service.generator.module.project.ProjectEntity;

import java.util.List;

/**
 * ModuleResponse
 *
 * @author f0rb
 * @date 2019-10-30
 */
@Getter
@Setter
public class ModuleResponse {

    private Integer id;

    private Integer projectId;

    private String name;

    private String displayName;

    private String modelName;

    private String fullName;

    private String tableName;

    private ProjectEntity projectEntity;

    private List<ColumnEntity> columnEntities;

}
