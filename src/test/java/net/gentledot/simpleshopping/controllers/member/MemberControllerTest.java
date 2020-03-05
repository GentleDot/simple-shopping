package net.gentledot.simpleshopping.controllers.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.member.Member;
import net.gentledot.simpleshopping.services.member.MemberService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("api/v1/member/checkIsExistedEmail 접속 시 이메일 중복 여부를 확인할 수 있다")
    void checkEmailExistedTest() throws Exception {
        // given
        String address = "testmail@test.com";
        String request = "{\"email\" : \"" + address + "\"}";

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/member/checkIsExistedEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.response").value(true))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("api/v1/member/checkIsExistedEmail 접속 시 이메일 주소가 없으면 exception 발생")
    void checkEmailExistedTestWithEmptyAddress() throws Exception {
        // given
        String address = "";
        String request = "{\"email\" : \"" + address + "\"}";

        // when
        //TODO IllegalArgumentException 처리 hander 고민 필요
        var exception = assertThrows(NestedServletException.class
                , () -> mockMvc.perform(get("/api/v1/member/checkIsExistedEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)));

        // then
//        assertThat(exception.getMessage(), is("이메일 주소는 반드시 존재해야 합니다."));

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
                .content(request));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString());
        log.debug("getResponse : {}", result.getResponse().getContentAsString());
        log.debug("response_member : {}", responseNode.get("response"));
    }

    @Test
    @DisplayName("api/v1/member/myInfo 를 통해 회원정보를 확인한다.")
    void myInfoTest() throws Exception {
        // given
        setTestMember();
        String address = "testmail@test.com";
        String password = "PROTECTED";

        String request = "{" +
                "\"email\" : \"" + address + "\"," +
                "\"password\" : \"" + password + "\"" +
                "}";

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/member/myInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // then
        MvcResult result = actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString());
        log.debug("getResponse : {}", result.getResponse().getContentAsString());
        log.debug("response_member : {}", responseNode.get("response"));
    }

    private void setTestMember(){
        String address = "testmail@test.com";
        Email email = new Email(address);
        String password = "PROTECTED";
        String name = "testName";

        Member join = memberService.join(email, password, name);
        log.debug("저장된 memeber : {}", join);
    }
}