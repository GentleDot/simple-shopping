package net.gentledot.simpleshopping.controllers;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("기본적으로 index가 생성되어 있는지 확인")
    void index() throws Exception {
        // when
        ResultActions actions = mockMvc.perform(get("/"));

        // then
        actions.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("index 페이지를 요청할 때 결과 확인")
    void bookIndexWithBookList() throws Exception {
        // given

        // when
        ResultActions actions = mockMvc.perform(get("/api/books"));

        // then
        actions.andDo(print())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("books", "status"))
                .andExpect(model().attribute("books", is(notNullValue())))
                .andExpect(status().isOk());

    }


}