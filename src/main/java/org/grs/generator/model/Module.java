package org.grs.generator.model;

import java.util.Date;
import java.util.List;

import org.grs.generator.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Module extends PageableModel<Module> {

    private Integer id;

    private Integer projectId;

    private String name;

    private String displayName;

    private String modelName;

    private String fullName;

    private String tableName;

    private Date createTime;

    //表单字段
    //update时不需要对createSql判空@NotNull(message = "建表sql不能为空")
    private String createSql;

    //返回字段
    private Project project;

    private List<Column> columns;

}