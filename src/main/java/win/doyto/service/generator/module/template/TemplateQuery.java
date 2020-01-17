package win.doyto.service.generator.module.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import win.doyto.query.core.PageQuery;

/**
 * TemplateQuery
 *
 * @author f0rb
 * @date 2019-10-30
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateQuery extends PageQuery {
    private Integer projectId;
}
