package org.grs.generator.mapper;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import org.grs.generator.model.{{gen.name | capitalize}};
import org.grs.generator.test.SpringTest;

import static org.junit.Assert.*;

/**
 * MapperTest类
 *
 * @author Yuanzhen on {{Date.now() | date:'yyyy-MM-dd'}}.
 */
@Slf4j
public class {{gen.name | capitalize}}MapperTest extends SpringTest {

    @Resource
    public {{gen.name | capitalize}}Mapper {{gen.name}}Mapper;

    @Test
    public void testCount() throws Exception {
        long count = {{gen.name}}Mapper.count(new {{gen.name | capitalize}}());
        log.info("结果总数：{}", count);
    }
}