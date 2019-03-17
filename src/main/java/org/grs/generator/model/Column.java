package org.grs.generator.model;



import org.grs.generator.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Column extends PageableModel<Column> {

    private Integer id;

    private Integer projectId;

    private String tableName;

    private String field;

    private String label;

    private String type;

    private Boolean nullable;

    private String key;

    private String comment;

    private Boolean valid;

    public void setNull(String s) {
        nullable = "YES".equals(s);
    }

}