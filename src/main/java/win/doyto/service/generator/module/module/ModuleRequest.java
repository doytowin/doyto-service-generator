package win.doyto.service.generator.module.module;

import lombok.Getter;
import lombok.Setter;
import win.doyto.query.validation.CreateGroup;

import javax.validation.constraints.NotNull;

/**
 * ModuleRequest
 *
 * @author f0rb
 * @date 2019-10-30
 */
@Getter
@Setter
public class ModuleRequest {

    private Integer id;

    private Integer projectId;

    private String name;

    private String displayName;

    private String modelName;

    private String fullName;

    private String tableName;

    @NotNull(message = "建表sql不能为空", groups = CreateGroup.class)
    private String createSql;

    private Boolean valid;

}
