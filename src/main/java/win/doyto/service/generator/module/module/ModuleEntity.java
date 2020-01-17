package win.doyto.service.generator.module.module;

import win.doyto.query.entity.CommonEntity;

import javax.persistence.Table;

@lombok.Getter
@lombok.Setter
@Table(name = "gen_module")
public class ModuleEntity extends CommonEntity<Integer, Integer> {

    private Integer projectId;

    private String name;

    private String displayName;

    private String modelName;

    private String fullName;

    private String tableName;

}