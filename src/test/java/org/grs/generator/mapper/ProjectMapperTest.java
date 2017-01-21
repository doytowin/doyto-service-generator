package org.grs.generator.mapper;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import org.grs.generator.model.Project;
import org.grs.generator.test.SpringTest;

/**
 * MapperTest类
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
public class ProjectMapperTest extends SpringTest {

    @Resource
    public ProjectMapper projectMapper;

    @Test
    public void testCount() throws Exception {
        long count = projectMapper.count(new Project());
        log.info("结果总数：{}", count);
    }
}