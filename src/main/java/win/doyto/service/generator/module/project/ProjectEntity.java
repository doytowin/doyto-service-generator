package win.doyto.service.generator.module.project;

import win.doyto.query.entity.CommonEntity;

import javax.persistence.Table;

@lombok.Getter
@lombok.Setter
@Table(name = "gen_project")
public class ProjectEntity extends CommonEntity<Integer, Integer> {

    private String name;

    private String path;

    private String jdbcDriver;

    private String jdbcUrl;

    private String jdbcUsername;

    private String jdbcPassword;

    private String tablePrefix;

}