package com.nikitalipatov.handmadeshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nikitalipatov.handmadeshop.dto.ProductFilterDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.nio.charset.StandardCharsets;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SearchTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void test() throws Exception {

        ProductFilterDTO productFilterDTO = new ProductFilterDTO();

        //productFilterDTO.setSearchText("Домик");

        productFilterDTO.setPriceFrom(500);
        String[] categories = {"Ошейники"};
        productFilterDTO.setCategories(categories);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(productFilterDTO);

        MvcResult result = this.mockMvc.perform(post("/api/products/filter").contentType(APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("content = " + content);
    }
}
