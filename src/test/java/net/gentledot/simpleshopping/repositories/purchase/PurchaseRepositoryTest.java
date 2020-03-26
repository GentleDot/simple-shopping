package net.gentledot.simpleshopping.repositories.purchase;

import net.gentledot.simpleshopping.models.purchase.Purchase;
import net.gentledot.simpleshopping.models.purchase.PurchaseDetail;
import net.gentledot.simpleshopping.models.purchase.PurchaseStatus;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PurchaseRepositoryTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PurchaseRepository purchaseRepository;

    @BeforeAll
    void setUp() {
        String address = "test@test.com";
        ArrayList<PurchaseDetail> items1 = new ArrayList<>();
        String item1 = "ess202003testitem";
        items1.add(new PurchaseDetail(item1, 99));
        ArrayList<PurchaseDetail> items2 = new ArrayList<>();
        String item2 = "ess202001testbook";
        items2.add(new PurchaseDetail(item2, 2));
        Purchase order1 = new Purchase.Builder(address, items1).build();
        Purchase order2 = new Purchase.Builder(address, items2).build();

        purchaseRepository.save(order1);
        purchaseRepository.save(order2);
    }


    @Test
    @DisplayName("주문 정보 저장")
    @Order(1)
    void savePurchaseTest() {
        String address = "test@test.com";
        ArrayList<PurchaseDetail> items = new ArrayList<>();
        String item1 = "ess201801testbook";
        String item2 = "ess201901testnote";
        items.add(new PurchaseDetail(item1, 2));
        items.add(new PurchaseDetail(item2, 4));
        Purchase newOrder = new Purchase.Builder(address, items).build();

        Purchase savedPurchase = purchaseRepository.save(newOrder);

        assertThat(savedPurchase, is(notNullValue()));
        assertThat(savedPurchase.getEmail(), is(address));
        assertThat(savedPurchase.getStatus(), is(PurchaseStatus.CONFIRM));
        assertThat(savedPurchase.getLastChangeAt(), is(nullValue()));
        assertThat(savedPurchase.getCreateAt(), is(notNullValue()));
        assertThat(savedPurchase.getItems().get(0).getGoodsId(), is(item1));
        assertThat(savedPurchase.getItems().get(0).getQuantity(), is(2));
        assertThat(savedPurchase.getItems().get(1).getGoodsId(), is(item2));
        assertThat(savedPurchase.getItems().get(1).getQuantity(), is(4));

        log.debug("저장된 주문 정보 : {}", savedPurchase);
        log.debug("생성 일시 : {}", savedPurchase.getCreateAt());
    }

    @Test
    @DisplayName("주문 정보 취소")
    @Order(2)
    void cancelPurchaseTest() {
        long targetId = 1L;

        Purchase purchase = purchaseRepository.findById(targetId).orElse(null);
        PurchaseStatus targetStatus = purchase.getStatus();
        log.debug("조회된 주문 내역 : {}", purchase);

        purchase.setStatusAndLastChangeAt(PurchaseStatus.CANCEL);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        assertThat(savedPurchase, is(notNullValue()));

        assertThat(savedPurchase.getStatus().equals(targetStatus), is(false));
        assertThat(savedPurchase.getStatus(), is(PurchaseStatus.CANCEL));

        log.debug("저장된 주문 정보 : {}", savedPurchase);
        log.debug("생성 일시 : {}", savedPurchase.getCreateAt());
    }

    @Test
    @DisplayName("맴버의 주문 정보 조회 (email로 확인)")
    @Order(3)
    void getPurchaseInfoTest() {
        String address = "test@test.com";

        List<Purchase> allByEmail = purchaseRepository.findAllByEmail(address);

        assertThat(allByEmail, is(notNullValue()));

        assertThat(allByEmail.get(1).getStatus(), is(PurchaseStatus.CONFIRM));

        log.debug("주문 정보 List: {}", allByEmail);
        log.debug("주문 정보 List_get(0): {}", allByEmail.get(0));
    }

}