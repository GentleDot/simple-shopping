package net.gentledot.simpleshopping.services.purchase;

import net.gentledot.simpleshopping.models.book.Book;
import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.purchase.Purchase;
import net.gentledot.simpleshopping.models.purchase.PurchaseDetail;
import net.gentledot.simpleshopping.models.purchase.PurchaseStatus;
import net.gentledot.simpleshopping.models.response.purchase.PurchaseResponse;
import net.gentledot.simpleshopping.repositories.book.BookRepository;
import net.gentledot.simpleshopping.repositories.purchase.PurchaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @MockBean
    PurchaseRepository purchaseRepository;

    @MockBean
    BookRepository bookRepository;

    @Test
    @DisplayName("주문 요청 시 요청한 상품을 확인 후 주문 처리 내역을 응답한다.")
    void addPurchaseTest() {
        // given
        String address = "testAccount@test.com";
        Email email = new Email(address);
        Map<String, Integer> orderRequest = new HashMap<>();
        String item1 = "ess201901testgoods";
        String item2 = "txb202001neworders";
        int item1Quantity = 1;
        int item2Quantity = 2;
        orderRequest.put(item1, item1Quantity);
        orderRequest.put(item2, item2Quantity);

        Purchase purchase = setTestPurchase(address);

        given(bookRepository.findbyBookId(anyString()))
                .willReturn(Optional.of(new Book.Builder("txb", "testObject", LocalDate.now()).build()));

        // when
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

        Purchase newOrder = new Purchase.Builder(email.getAddress(), new ArrayList<>()).build();
        orderRequest.forEach((goods, quantity) -> {
            bookRepository.findbyBookId(goods)
                    .orElseThrow(() -> new RuntimeException("해당 id의 상품이 없습니다."));
            newOrder.addGoodsInItems(new PurchaseDetail(goods, quantity));
        });

        Purchase savedPurchase = purchaseRepository.save(newOrder);
        log.debug("저장된 주문 내역 : {}", savedPurchase);

        // then
        PurchaseResponse purchaseResponse = new PurchaseResponse.Builder()
                .id(savedPurchase.getId())
                .lastChangeAt(savedPurchase.getLastChangeAt())
                .purchaseDate(savedPurchase.getCreateAt())
                .status(savedPurchase.getStatus())
                .build();

        assertThat(purchaseResponse, is(notNullValue()));
        log.debug("주문 내역 응답 : {}", purchaseResponse);
    }

    @Test
    @DisplayName("주문 취소 요청 시 주문 상태를 취소로 변경하고 저장한다.")
    void cancelPurchaseTest() {
        // given
        String address = "testAccount@test.com";
        Email email = new Email(address);
        Long purchaseId = 0L;

        Purchase purchase = setTestPurchase(address);
        given(purchaseRepository.findById(anyLong())).willReturn(Optional.of(purchase));

        // when
        purchase.setStatusAndLastChangeAt(PurchaseStatus.CANCEL);
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

        Purchase getPurchaseById = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("해당하는 ID의 주문 내역이 존재하지 않습니다."));
        getPurchaseById.setStatusAndLastChangeAt(PurchaseStatus.CANCEL);

        Purchase savedPurchase = purchaseRepository.save(getPurchaseById);
        log.debug("저장된 주문 내역 : {}", savedPurchase);

        // then
        PurchaseResponse purchaseResponse = new PurchaseResponse.Builder()
                .id(savedPurchase.getId())
                .lastChangeAt(savedPurchase.getLastChangeAt())
                .purchaseDate(savedPurchase.getCreateAt())
                .status(savedPurchase.getStatus())
                .build();

        assertThat(purchaseResponse, is(notNullValue()));
        log.debug("주문 내역 응답 : {}", purchaseResponse);
    }

    @Test
    @DisplayName("로그인 사용자의 이메일로 등록된 주문 내역을 조회한다.")
    void getPurchasesInfoTest() {
        // given
        String address = "testAccount@test.com";
        Email email = new Email(address);

        List<Purchase> purchaseList = List.of(setTestPurchase(address));

        // when
        when(purchaseRepository.findAllByEmail(address)).thenReturn(purchaseList);

        List<Purchase> getPurchaseListByEmail = purchaseRepository.findAllByEmail(email.getAddress());

        // then
        assertThat(getPurchaseListByEmail, is(notNullValue()));
        assertThat(getPurchaseListByEmail.get(0).getStatus(), is(PurchaseStatus.CONFIRM));

        log.debug("조회한 사용자의 주문 내역 리스트 : {}", getPurchaseListByEmail);
    }

    private Purchase setTestPurchase(String address){
        String item1 = "ess201901testgoods";
        String item2 = "txb202001neworders";
        int item1Quantity = 1;
        int item2Quantity = 2;

        return new Purchase.Builder(address,
                List.of(new PurchaseDetail(item1, item1Quantity), new PurchaseDetail(item2, item2Quantity)))
                .build();
    }
}