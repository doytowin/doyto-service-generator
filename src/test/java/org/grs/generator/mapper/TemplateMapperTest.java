package org.grs.generator.mapper;

import java.util.List;
import javax.annotation.Resource;

import org.grs.generator.model.Template;
import org.grs.generator.test.SpringTest;
import org.junit.Test;

/**
 * MapperTest类
 *
 * @author Yuanzhen on 2016-07-22.
 */
public class TemplateMapperTest extends SpringTest {
    @Test
    public void query() throws Exception {
        Template query = new Template();
        query.setLimit(2);
        List list = templateMapper.query(query);
        System.out.println(list);
    }

    @Resource
    public TemplateMapper templateMapper;

    @Test
    public void testCount() throws Exception {
        int count = templateMapper.count(new Template());
        System.out.println(count);
    }
}