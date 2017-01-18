package org.grs.generator.mapper;

import java.util.List;
import javax.annotation.Resource;

import org.grs.generator.model.Column;
import org.grs.generator.test.SpringTest;
import org.junit.Test;

/**
 * MapperTest类
 *
 * @author Yuanzhen on 2016-07-23.
 */
public class ColumnMapperTest extends SpringTest {
    @Test
    public void test_saveColumns() throws Exception {

        Column query = new Column();
        query.setTableName("GEN_User");
        List<Column> columns = columnMapper.query(query);
        for (Column column : columns) {
            if (column.getLabel() == null) {
                column.setLabel(column.getField() + "_1");
            }
        }
        columnMapper.saveColumns(columns);
    }

    @Resource
    public ColumnMapper columnMapper;

    @Test
    public void testCount() throws Exception {
        long count = columnMapper.count(new Column());
        System.out.println(count);
    }
}