package org.grs.generator.model;



import org.grs.generator.common.PageableModel;

@lombok.Getter
@lombok.Setter
public class Column extends PageableModel<Column> {

    private Integer id;

    private String tableName;

    private String field;

    private String label;

    private String type;

    private Boolean nullable;

    private String key;

    public void setNull(String s) {
        nullable = "YES".equals(s);
        //System.out.println("setNull " + s);
    }

}