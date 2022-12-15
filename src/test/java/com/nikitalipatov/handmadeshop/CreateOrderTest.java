package com.nikitalipatov.handmadeshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nikitalipatov.handmadeshop.dto.OrderDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CreateOrderTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void test() throws Exception {

        List<UUID> products = new ArrayList<>();
        products.add(UUID.fromString("cab4e319-d6ae-49c9-89ab-984fdca0b30d"));
        OrderDTO orderDTO = new OrderDTO(products, "Самовывоз",
                "Липатов", "Никита", "пр-кт 40-летия Победы",
                "9515259132");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(orderDTO);

    MvcResult result = this.mockMvc.perform(post("/api/order/add")
                    .contentType(APPLICATION_JSON)
                    .content(requestJson)
                    .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjcwNTc0NTM5LCJleHAiOjE2NzA2NjA5Mzl9.HRBJ8UrqkQ7HEvMffElrd7WTP7OO-Xrjpxk3eRj_E6eEutU9gd9xSmZrnMqagU3sh5xtyRSSZb3Uui2ZXYGOdQ"))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

    String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("content = " + content);
}
}

