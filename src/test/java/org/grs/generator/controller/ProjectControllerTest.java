package org.grs.generator.controller;

import org.grs.generator.test.SpringTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ControllerTest类
 *
 * @author Yuanzhen on 2016-07-22.
 */
public class ProjectControllerTest extends SpringTest {

    @Test
    public void post_project_query_Test() throws Exception {
        RequestBuilder requestBuilder = get("/api/project")
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.result").isArray())
               .andReturn();
    }
}