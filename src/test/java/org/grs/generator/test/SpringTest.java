package org.grs.generator.test;

import javax.annotation.Resource;

import org.grs.generator.GeneratorWebApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * 类描述。
 *
 * @author Yuanzhen on 2015-06-29.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeneratorWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@Rollback
@ActiveProfiles("test")
@Transactional
public abstract class SpringTest {
    @Resource
    private WebApplicationContext wac;
    protected MockMvc mockMvc;

    @Before
    public void setup() {
        //WebContextFilter webContextFilter = new WebContextFilter();
        //mockMvc = MockMvcBuilders.webAppContextSetup(wac)
        //                         .addFilter(webContextFilter, "/*")
        //                         .build();

    }
}
