package win.doyto.service.generator.module.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import win.doyto.query.core.PageQuery;

/**
 * ModuleQuery
 *
 * @author f0rb
 * @date 2019-10-30
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleQuery extends PageQuery {

    private Integer projectId;

    private String tableName;
}
