package org.grs.generator.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.grs.generator.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Template extends PageableModel<Template> {

    private Integer id;

    @NotNull
    private Integer projectId;

    @NotNull
    private String suffix;

    private String path;

    @NotNull
    private Boolean cap;

    @NotNull
    private String content;

    private Boolean valid;

    private Date createTime;

    private Project project;

}