package win.doyto.service.generator.module.template;

import win.doyto.query.entity.AbstractCommonEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@lombok.Getter
@lombok.Setter
@Entity(name = "gen_template")
public class TemplateEntity extends AbstractCommonEntity<Integer, Integer> {

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