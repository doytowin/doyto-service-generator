package org.grs.generator.model;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.grs.generator.service.ProjectService;
import org.junit.Test;
import org.springframework.test.annotation.Commit;

import org.grs.generator.test.SpringUnitTest;

/**
 * ProjectServiceTest
 *
 * @author f0rb on 2017-01-21.
 */
@Slf4j
public class ProjectServiceTest extends SpringUnitTest {
    @Resource
    ProjectService projectService;

    @Test
    @Commit
    public void importAll() throws Exception {
        projectService.importDatabase(1);
    }

}