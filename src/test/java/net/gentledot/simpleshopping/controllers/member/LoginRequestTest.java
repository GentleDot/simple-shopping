package net.gentledot.simpleshopping.controllers.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriPort = 8443)
@ExtendWith(RestDocumentationExtension.class)
public class LoginRequestTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("로그인 요청을 통해 header에서 발급된 JWT를 확인할 수 있음")
    void getJwtTest() throws Exception {
        // 인증을 위한 JWT token 발급
        ObjectMapper objectMapper = new ObjectMapper();
        String loginRequest = "{\"username\" : \"gentledot.wp@gmail.com\", " +
                "\"password\" : \"pass\"" +
                "}";

        ResultActions result = mockMvc.perform(post("/login")
                .content(loginRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .secure(true));

        result.andDo(print())
                .andDo(document("login-request",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시"),
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type")
                        ),
                        requestFields(
                                fieldWithPath("username").description("로그인 요청 - 사용자 ID (Email Address)"),
                                fieldWithPath("password").description("로그인 요청 - 사용자 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName("auth").description("(로그인 성공 시) 사용자 인증 토큰 (JWT)")
                        )));

        String testToken = result.andReturn()
                .getResponse().getHeaderValue("auth").toString();

        log.debug("발급된 JWT : {}", Optional.ofNullable(testToken).orElse(null));
    }
}
