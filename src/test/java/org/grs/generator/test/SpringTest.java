package org.grs.generator.test;

import org.grs.generator.GeneratorWebApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * 类描述。
 *
 * @author Yuanzhen on 2015-06-29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = GeneratorWebApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
@Rollback
@ActiveProfiles("test")
@Transactional
public abstract class SpringTest {
    @Autowired
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
