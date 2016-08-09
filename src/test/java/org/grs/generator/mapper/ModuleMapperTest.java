package org.grs.generator.mapper;

import java.util.List;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.grs.generator.model.Module;
import org.grs.generator.test.SpringTest;
import org.junit.Test;

/**
 * MapperTest类
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
public class ModuleMapperTest extends SpringTest {
    @Test
    public void query() throws Exception {
        Module query = new Module();
        query.setTableName("GEN_User");
        List modules = moduleMapper.query(query);
        log.info("{}", modules);
    }

    @Resource
    public ModuleMapper moduleMapper;

    @Test
    public void testCount() throws Exception {
        int count = moduleMapper.count(new Module());
        System.out.println(count);
    }
}