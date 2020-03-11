package net.gentledot.simpleshopping.controllers.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.simpleshopping.models.book.Book;
import net.gentledot.simpleshopping.repositories.book.BookRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BookRepository bookRepository;

    @BeforeAll
    void setUp() {
        String categoryCode = "ess";
        String name = "test";
        LocalDate publishDate = LocalDate.of(2019, 2, 3);
        Book book = new Book.Builder(categoryCode, name, publishDate)
                .build();

        if (bookRepository.findbyBookId(book.getId()).isEmpty()) {
            bookRepository.save(book);
        }
    }

    @Test
    @DisplayName("상품 신규 등록")
    void registBookTest() throws Exception {
        // given
        String request = "{\"category\" : \"essay\"," +
                "\"name\" : \"test book\"," +
                "\"description\" : \"\"," +
                "\"date\" : \"2019.04.07\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(post("/api/v1/book/regist")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andExpect(jsonPath("$.response.description").isEmpty())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        log.debug("getResponse : {}", responseNode);
        log.debug("response_member : {}", responseNode.get("response"));
    }

    @Test
    @DisplayName("저장된 Book 정보 중 Description 수정")
    void updateBookDescriptionTest() throws Exception {
        // given
        String desc = "이것은 description 변경의 예입니다.";
        String request = "{\"category\" : \"essay\"," +
                "\"name\" : \"test\"," +
                "\"description\" : \"" + desc + "\"," +
                "\"date\" : \"2019-02-03\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(patch("/api/v1/book/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andExpect(jsonPath("$.response.description").isNotEmpty())
                .andExpect(jsonPath("$.response.description").value(desc))
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        log.debug("getResponse : {}", responseNode);
        log.debug("response_member : {}", responseNode.get("response"));


    }

    @Test
    @DisplayName("상품 정보 조회")
    void getBookInfoTest() throws Exception {
        // given
        String request = "{\"category\" : \"essay\"," +
                "\"name\" : \"test\"," +
                "\"date\" : \"2019-02-03\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/book/info")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andExpect(jsonPath("$.response.description").isEmpty())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        log.debug("getResponse : {}", responseNode);
        log.debug("response_member : {}", responseNode.get("response"));
    }

    @Test
    @DisplayName("카테고리별 상품 리스트 조회")
    void getBookListByCategoryTest() throws Exception {
        // given
        String category = "essay";

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/book/list/" + category));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andExpect(jsonPath("$.response[0].description").isEmpty())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        log.debug("getResponse : {}", responseNode);
        log.debug("response_member : {}", responseNode.get("response"));
    }

    @Test
    @DisplayName("상품 정보 삭제")
    void deleteBook() throws Exception {
        // given
        String request = "{\"category\" : \"essay\"," +
                "\"name\" : \"test\"," +
                "\"date\" : \"2019-02-03\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(delete("/api/v1/book/delete")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.response").value(true));
    }
}