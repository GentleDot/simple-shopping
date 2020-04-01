package net.gentledot.simpleshopping.controllers.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.services.member.MemberService;
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
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriPort = 8443)
@ExtendWith(RestDocumentationExtension.class)
class MemberControllerTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    private FieldDescriptor[] requestMemberFields = new FieldDescriptor[]{
            fieldWithPath("email").description("사용자 ID (Email Address)"),
            fieldWithPath("password").description("사용자 비밀번호")
    };

    private FieldDescriptor[] responseMemberFields = new FieldDescriptor[]{
            fieldWithPath("processSuccess").description("(요청 성공 시) 요청의 처리 결과"),
            fieldWithPath("response.email").description("사용자 ID (Email Address)"),
            fieldWithPath("response.name").description("사용자명").optional(),
            fieldWithPath("response.lastLoginAt").description("최종 로그인 일시").optional(),
            fieldWithPath("response.createAt").description("생성 일시"),
            fieldWithPath("error").description("(요청 실패 시) 요청의 오류 메시지와 상태 코드"),
    };

    @Test
    @DisplayName("api/v1/member/checkIsExistedEmail 접속 시 이메일 중복 여부를 확인할 수 있다")
    void checkEmailExistedTest() throws Exception {
        // given
        String address = "gentledot.wp@gmail.com";
        String request = "{\"email\" : \"" + address + "\"}";

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/member/checkIsExistedEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .secure(true));

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.response").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andDo(document("member/check-member-mail",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시"),
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type")
                        ),
                        requestFields(
                                fieldWithPath("email").description("요청 email address")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                fieldWithPath("processSuccess").description("(요청 성공 시) 요청의 처리 결과"),
                                fieldWithPath("response").description("요청한 이메일 주소의 존재 여부"),
                                fieldWithPath("error").description("(요청 실패 시) 요청의 오류 메시지와 상태 코드")
                        )));
    }

    @Test
    @DisplayName("api/v1/member/checkIsExistedEmail 접속 시 이메일 주소가 없으면 exception 발생")
    void checkEmailExistedTestWithEmptyAddress() throws Exception {
        // given
        String address = "";
        String request = "{\"email\" : \"" + address + "\"}";

        // when
        var exception = assertThrows(NestedServletException.class
                , () -> mockMvc.perform(get("/api/v1/member/checkIsExistedEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .secure(true)));
    }

    @Test
    @DisplayName("api/v1/member/join 을 통해 회원 가입 요청을 처리한다.")
    void joinTest() throws Exception {
        // given
        String address = "newMember@test.com";
        String password = "PROTECTED";
        String name = "welcomeMember";
        String request = "{" +
                "\"email\" : \"" + address + "\"," +
                "\"password\" : \"" + password + "\"," +
                "\"name\" : \"" + name + "\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(post("/api/v1/member/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .secure(true));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andDo(document("member/join-member",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시"),
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type")
                        ),
                        requestFields(
                                fieldWithPath("name").description("사용자 이름 (optional)")
                        ).and(requestMemberFields),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                responseMemberFields
                        ))
                ).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString());
        log.debug("getResponse : {}", result.getResponse().getContentAsString());
        log.debug("response_member : {}", responseNode.get("response"));
    }

    @Test
    @DisplayName("api/v1/member/myInfo 를 통해 회원정보를 확인한다.")
    void myInfoTest() throws Exception {
        // given
        String address = "gentledot.wp@gmail.com";
        String password = "pass";
        Email email = new Email(address);
        String name = "testName";

        String request = "{" +
                "\"email\" : \"" + address + "\"," +
                "\"password\" : \"" + password + "\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/member/myInfo")
                .header("auth", getTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .secure(true));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andDo(document("member/get-member",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시"),
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type"),
                                headerWithName("auth").description("접속 인증 정보가 담긴 JWT")
                        ),
                        requestFields(
                                requestMemberFields
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                responseMemberFields
                        )))
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString());
        log.debug("getResponse : {}", result.getResponse().getContentAsString());
        log.debug("response_member : {}", responseNode.get("response"));
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