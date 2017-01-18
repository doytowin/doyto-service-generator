package org.grs.generator.mapper;

import java.util.List;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.grs.generator.model.Template;
import org.grs.generator.test.SpringTest;
import org.junit.Test;

/**
 * MapperTest类
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
public class TemplateMapperTest extends SpringTest {
    @Test
    public void query() throws Exception {
        Template query = new Template();
        query.setLimit(2);
        List list = templateMapper.query(query);
        System.out.println(list);
    }

    @Test
    public void queryTest() throws Exception {

        Template query = new Template();
        query.setLimit(6);
        List list = templateMapper.query(query);
        log.info("{}", JSON.toJSONString(list, true));
    }

    @Test
    public void queryTest2() throws Exception {
        Template query = new Template();
        query.setLimit(10);
        List list = templateMapper.query(query);
        log.info("{}", JSON.toJSONString(list, true));
    }

    @Resource
    public TemplateMapper templateMapper;

    @Test
    public void testCount() throws Exception {
        long count = templateMapper.count(new Template());
        System.out.println(count);
    }

    @Test
    public void testGet() throws Exception {
        Template template = templateMapper.get(1);
        log.info("path:{}", template.getPath());
        template.setPath("test");

        Template template2 = templateMapper.get(1);
        log.info("path:{}", template2.getPath());
        template.setPath("test2");

        Template template3 = templateMapper.get(1);
        log.info("path:{}", template3.getPath());

        log.info("{}", template == template2);
    }
}