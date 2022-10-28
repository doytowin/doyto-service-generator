package win.doyto.service.generator.module.project;

import win.doyto.query.entity.AbstractCommonEntity;

import javax.persistence.Entity;

@lombok.Getter
@lombok.Setter
@Entity(name = "gen_project")
public class ProjectEntity extends AbstractCommonEntity<Integer, Integer> {

    private String name;

    private String path;

    private String jdbcDriver;

    private String jdbcUrl;

    private String jdbcUsername;

    private String jdbcPassword;

    private String tablePrefix;

}