package org.grs.generator.mapper;

import java.util.List;
import javax.annotation.Resource;

import org.grs.generator.model.{{gen.name | capitalize}};
import org.grs.generator.test.SpringTest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * MapperTest类
 *
 * @author Yuanzhen on {{Date.now() | date:'yyyy-MM-dd'}}.
 */
public class {{gen.name | capitalize}}MapperTest extends SpringTest {

    @Resource
    public {{gen.name | capitalize}}Mapper {{gen.name}}Mapper;

    @Test
    public void testCount() throws Exception {
        int count = {{gen.name}}Mapper.count(new {{gen.name | capitalize}}());
        System.out.println(count);
    }
}