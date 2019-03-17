package org.grs.generator.model;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.grs.generator.service.ModuleService;
import org.junit.Test;

import org.grs.generator.test.SpringUnitTest;

/**
 * ModuleServiceTest
 *
 * @author f0rb on 2017-01-21.
 */
@Slf4j
public class ModuleServiceTest extends SpringUnitTest {
    @Resource
    ModuleService moduleService;

    @Test
    public void importModule() throws Exception {
        moduleService.importModule(1,
                "CREATE TABLE test_category\n" +
                        "(\n" +
                        "    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                        "    name VARCHAR(45) NOT NULL,\n" +
                        "    memo VARCHAR(225),\n" +
                        "    zoneId INT(11) NOT NULL,\n" +
                        "    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL\n" +
                        ")");
    }

}