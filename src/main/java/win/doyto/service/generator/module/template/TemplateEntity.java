package win.doyto.service.generator.module.template;

import win.doyto.query.entity.CommonEntity;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@lombok.Getter
@lombok.Setter
@Table(name = "gen_template")
public class TemplateEntity extends CommonEntity<Integer, Integer> {

    @NotNull
    private Integer projectId;

    @NotNull
    private String suffix;

    private String path;

    @NotNull
    private Boolean cap;

    @NotNull
    private String content;

    private Boolean valid = true;
}