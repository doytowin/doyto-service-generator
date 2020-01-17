package win.doyto.service.generator.module.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import win.doyto.query.core.PageQuery;

/**
 * ProjectQuery
 *
 * @author f0rb
 * @date 2019-10-30
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectQuery extends PageQuery {
    private String nameLike;
}
