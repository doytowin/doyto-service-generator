package org.grs.generator.model;

import java.util.Date;

import org.grs.generator.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Project extends PageableModel<Project> {

    private Integer id;

    private Integer userId;

    private String name;

    private String path;

    private String jdbcDriver;

    private String jdbcUrl;

    private String jdbcUsername;

    private String jdbcPassword;

    private String tablePrefix;

    private Date createTime;

}