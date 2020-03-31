package net.gentledot.simpleshopping.controllers.purchase;

import net.gentledot.simpleshopping.models.purchase.Purchase;
import net.gentledot.simpleshopping.models.purchase.PurchaseDetail;
import net.gentledot.simpleshopping.repositories.purchase.PurchaseRepository;
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
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

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
@ExtendWith(RestDocumentationExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PurchaseControllerTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PurchaseRepository purchaseRepository;

    private FieldDescriptor[] responsePurchaseResultFields = new FieldDescriptor[]{
            fieldWithPath("processSuccess").description("(요청 성공 시) 요청의 처리 결과"),
            fieldWithPath("response.id").description("주문 내역의 ID"),
            fieldWithPath("response.purchaseDate").description("주문 일자"),
            fieldWithPath("response.status").description("주문 상태"),
            fieldWithPath("response.lastChangeAt").description("주문 상태 변경일 (주문 확정에서 변경되는 경우 기록)").optional(),
            fieldWithPath("error").description("(요청 실패 시) 요청의 오류 메시지와 상태 코드"),
    };

    private FieldDescriptor[] responsePurchaseFields = new FieldDescriptor[]{
            fieldWithPath("processSuccess").description("(요청 성공 시) 요청의 처리 결과"),
            fieldWithPath("response[].id").description("주문 내역의 ID"),
            fieldWithPath("response[].status").description("주문 상태"),
            fieldWithPath("response[].lastChangeAt").description("주문 상태 변경일 (주문 확정에서 변경되는 경우 기록)").optional(),
            fieldWithPath("response[].createAt").description("주문 일자"),
            fieldWithPath("response[].items[].seq").description("주문 상품의 순번"),
            fieldWithPath("response[].items[].goodsId").description("주문 상품의 상품 ID"),
            fieldWithPath("response[].items[].quantity").description("주문 상품의 수량"),
            fieldWithPath("error").description("(요청 실패 시) 요청의 오류 메시지와 상태 코드"),
    };



    @BeforeAll
    void setUp() {
        // test 용도 주문 내역 저장
        String emailAddress = "gentledot.wp@gmail.com";
        Purchase testPurchase = new Purchase.Builder(emailAddress, new ArrayList<>()).build();
        testPurchase.addGoodsInItems(new PurchaseDetail("txb201901testgoods", 1));

        if (purchaseRepository.findAllByEmail(emailAddress).isEmpty()) {
            purchaseRepository.save(testPurchase);
        }
    }

    @Test
    @DisplayName("신규 주문 요청 - 주문 내역 저장")
    void addPurchase() throws Exception {
        // given
        String request = "{\"order\" : {\"ess201901testbook\" : \"10\"}}";

        // when
        ResultActions actions = mockMvc.perform(post("/api/v1/purchase/add")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .header("auth", getTestToken())
                .secure(true));

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andDo(document("purchase/add",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type과 문자열 인코딩 명시"),
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type"),
                                headerWithName("auth").description("접속 인증 정보가 담긴 JWT")
                        ),
                        requestFields(
                                subsectionWithPath("order").description("주문 상품의 상품 id와 상품 갯수를 전송하여 주문 요청")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                responsePurchaseResultFields
                        )))
                .andReturn();
    }

    @Test
    @DisplayName("주문 내역 상태 변경 - 주문 취소 설정")
    void cancelPurchase() throws Exception {
        // given
        String accessToken = getTestToken();

        // when
        ResultActions actions = mockMvc.perform(put("/api/v1/purchase/cancel/1")
                .accept(MediaType.APPLICATION_JSON)
                .header("auth", accessToken)
                .secure(true));

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andDo(document("purchase/cancel",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type"),
                                headerWithName("auth").description("접속 인증 정보가 담긴 JWT")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                responsePurchaseResultFields
                        )))
                .andReturn();
    }

    @Test
    @DisplayName("로그인 사용자의 주문 내역 전체를 조회")
    void getMyPurchaseList() throws Exception {
        // given
        String accessToken = getTestToken();

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/member/myPurchases")
                .accept(MediaType.APPLICATION_JSON)
                .header("auth", accessToken)
                .secure(true));

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processSuccess").value(true))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.response").isNotEmpty())
                .andDo(document("purchase/list",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("서버에서 요구되는 Content type"),
                                headerWithName("auth").description("접속 인증 정보가 담긴 JWT")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type과 문자열 인코딩 명시")
                        ),
                        responseFields(
                                responsePurchaseFields
                        )))
                .andReturn();
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