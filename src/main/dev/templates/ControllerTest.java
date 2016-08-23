package org.grs.generator.controller;

import org.grs.generator.test.SpringTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * ControllerTest类
 *
 * @author Yuanzhen on {{Date.now() | date:'yyyy-MM-dd'}}.
 */
public class {{gen.name | capitalize}}ControllerTest extends SpringTest {

    @Test
    public void post_{{gen.name}}_query_Test() throws Exception {
        RequestBuilder requestBuilder = get("/api/{{gen.name}}")
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.result").isArray())
               .andReturn();
    }
}