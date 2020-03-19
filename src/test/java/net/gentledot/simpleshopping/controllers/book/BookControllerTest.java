package net.gentledot.simpleshopping.controllers.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.simpleshopping.models.book.Book;
import net.gentledot.simpleshopping.repositories.book.BookRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriPort = 8443)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RestDocumentationExtension.class)
class BookControllerTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BookRepository bookRepository;

    private FieldDescriptor[] requestBookFields = new FieldDescriptor[]{
            fieldWithPath("category").description("도서 상품의 카테고리"),
            fieldWithPath("name").description("도서 상품의 명칭"),
            fieldWithPath("description").description("도서 상품의 설명 (없을 수도 있음)").optional(),
            fieldWithPath("date").description("도서 상품의 발간일")
    };

    private FieldDescriptor[] responseBookFields = new FieldDescriptor[]{
            fieldWithPath("processSuccess").description("(요청 성공 시) 요청의 처리 결과"),
            fieldWithPath("response.category").description("도서 상품의 카테고리"),
            fieldWithPath("response.name").description("도서 상품의 명칭"),
            fieldWithPath("response.description").description("도서 상품의 설명 (optional)").optional(),
            fieldWithPath("response.publishDate").description("도서 상품의 발간일"),
            fieldWithPath("error").description("(요청 실패 시) 요청의 오류 메시지와 상태 코드"),
    };



    @BeforeAll
    void setUp() {
        // test book 저장
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
                .content(request)
                .header("auth", getTestToken())
                .secure(true));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andExpect(jsonPath("$.response.description").isEmpty())
                .andDo(document("books/regist-book",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시"),
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type"),
                                headerWithName("auth").description("접속 인증 정보가 담긴 JWT")
                        ),
                        requestFields(
                                requestBookFields
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                responseBookFields
                        )))
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
                .content(request)
                .header("auth", getTestToken())
                .secure(true));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andExpect(jsonPath("$.response.description").isNotEmpty())
                .andExpect(jsonPath("$.response.description").value(desc))
                .andDo(document("books/update-book",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시"),
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type"),
                                headerWithName("auth").description("접속 인증 정보가 담긴 JWT")
                        ),
                        requestFields(
                                requestBookFields
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                responseBookFields
                        )))
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
                "\"description\" : \"\"," +
                "\"date\" : \"2019-02-03\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/book/info")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .header("auth", getTestToken())
                .secure(true));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andExpect(jsonPath("$.response.description").isEmpty())
                .andDo(document("books/get-book",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시"),
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type"),
                                headerWithName("auth").description("접속 인증 정보가 담긴 JWT")
                        ),
                        requestFields(
                                requestBookFields
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                responseBookFields
                        )))
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
        ResultActions actions = mockMvc.perform(get("/api/v1/book/list/" + category)
                .header("auth", getTestToken())
                .secure(true));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andExpect(jsonPath("$.response[0].description").isEmpty())
                .andDo(document("books/get-book-list",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName("auth").description("접속 인증 정보가 담긴 JWT")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                fieldWithPath("processSuccess").description("(요청 성공 시) 요청의 처리 결과"),
                                fieldWithPath("response[].category").description("도서 상품의 카테고리"),
                                fieldWithPath("response[].name").description("도서 상품의 명칭"),
                                fieldWithPath("response[].description").description("도서 상품의 설명 (optional)").optional(),
                                fieldWithPath("response[].publishDate").description("도서 상품의 발간일"),
                                fieldWithPath("error").description("(요청 실패 시) 요청의 오류 메시지와 상태 코드")
                        )))
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        log.debug("getResponse : {}", responseNode);
        log.debug("response_member : {}", responseNode.get("response"));
    }

    /*@Test
    @DisplayName("상품 정보 조회 시 없는 상품을 조회 요청했을 경우")
    void getBookInfoTestWithEmptyGoods() throws Exception {
        // given
        String request = "{\"category\" : \"essay\"," +
                "\"name\" : \"empty\"," +
                "\"date\" : \"2019-12-24\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/book/info")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .header("auth", getTestToken())
                .secure(true));

        // then
        actions.andDo(print());
    }*/

    @Test
    @DisplayName("상품 정보 삭제")
    void deleteBook() throws Exception {
        // given
        String request = "{\"category\" : \"essay\"," +
                "\"name\" : \"test\"," +
                "\"description\" : \"\"," +
                "\"date\" : \"2019-02-03\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(delete("/api/v1/book/delete")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .header("auth", getTestToken())
                .secure(true));

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.response").value(true))
                .andDo(document("books/delete-book",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시"),
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type"),
                                headerWithName("auth").description("접속 인증 정보가 담긴 JWT")
                        ),
                        requestFields(
                                requestBookFields
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                fieldWithPath("processSuccess").description("(요청 성공 시) 요청의 처리 결과"),
                                fieldWithPath("response").description("도서 정보 삭제 처리 결과"),
                                fieldWithPath("error").description("(요청 실패 시) 요청의 오류 메시지와 상태 코드")
                        )));
    }

    // 인증을 위한 JWT token 발급
    private String getTestToken() throws Exception {
        String loginRequest = "{\"username\" : \"gentledot.wp@gmail.com\", " +
                "\"password\" : \"pass\"" +
                "}";

        ResultActions result = mockMvc.perform(post("/login")
                .content(loginRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .secure(true));

        String token = result.andReturn()
                .getResponse().getHeaderValue("auth").toString();

        log.debug("발급된 token : {}", token);

        return token;
    }
}