package org.grs.generator.test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import org.grs.generator.GeneratorWebApplication;

/**
 * 类描述。
 *
 * @author Yuanzhen on 2015-06-29.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeneratorWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext
@Rollback
@ActiveProfiles("test")
@Transactional
public abstract class SpringUnitTest {
}
