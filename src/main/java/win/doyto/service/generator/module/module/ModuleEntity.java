package win.doyto.service.generator.module.module;

import win.doyto.query.entity.AbstractCommonEntity;

import javax.persistence.Entity;

@lombok.Getter
@lombok.Setter
@Entity(name = "gen_module")
public class ModuleEntity extends AbstractCommonEntity<Integer, Integer> {

    private Integer projectId;

    private String name;

    private String displayName;

    private String modelName;

    private String fullName;

    private String tableName;

}