package com.nikitalipatov.handmadeshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nikitalipatov.handmadeshop.payload.request.SignupRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegisterUserTest {
        @Autowired
        private MockMvc mockMvc;
        @Test
        public void test() throws Exception {

            Set<String> role = new HashSet<>();
            role.add("ROLE_USER");
            SignupRequest request = new SignupRequest("test111122", "nicklipa146@gmail.com", role, "testtest", "TestName", "TestSurname");

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
            String requestJson = ow.writeValueAsString(request);

            MvcResult result = this.mockMvc.perform(post("/api/auth/signup").contentType(APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().is4xxClientError())
                    .andDo(print())
                    .andReturn();

            String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            System.out.println("content = " + content);
//            assertEquals("Пользователь успешно зарегистрирован!", content);
            assertEquals("{\"message\":\"Данный электронный адрес уже используется!\"}", content);
            //assertEquals("{\"message\":\"Данный логин уже используется!\"}", content);
        }
}
