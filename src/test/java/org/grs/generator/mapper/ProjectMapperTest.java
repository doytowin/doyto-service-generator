package org.grs.generator.mapper;

import javax.annotation.Resource;

import org.grs.generator.model.Project;
import org.grs.generator.test.SpringTest;
import org.junit.Test;

/**
 * MapperTest类
 *
 * @author Yuanzhen on 2016-07-22.
 */
public class ProjectMapperTest extends SpringTest {

    @Resource
    public ProjectMapper projectMapper;

    @Test
    public void testCount() throws Exception {
        long count = projectMapper.count(new Project());
        System.out.println(count);
    }
}