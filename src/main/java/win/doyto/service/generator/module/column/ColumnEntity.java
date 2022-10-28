package win.doyto.service.generator.module.column;

import win.doyto.query.entity.AbstractCommonEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@lombok.Getter
@lombok.Setter
@Entity(name = "gen_column")
public class ColumnEntity extends AbstractCommonEntity<Integer, Integer> {

    private Integer projectId;

    private String tableName;

    private String field;

    private String label;

    private String type;

    private Boolean nullable;

    @Column(name = "`key`")
    private String keyName;

    private String comment;

    private Boolean valid;

    public void setNull(String s) {
        nullable = "YES".equals(s);
    }

}