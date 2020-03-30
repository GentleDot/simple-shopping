package net.gentledot.simpleshopping.models.purchase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class PurchaseTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    @DisplayName("주문 내역 객체 생성")
    void createPurchaseTest() {
        String address = "test@test.com";
        ArrayList<PurchaseDetail> items = new ArrayList<>();
        Purchase purchase = new Purchase.Builder(address, items).build();

        assertThat(purchase, is(notNullValue()));
        assertThat(purchase.getId(), is(0L));
        assertThat(purchase.getEmailAdress(), is(address));
        assertThat(purchase.getItems(), is(items));
        assertThat(purchase.getStatus(), is(PurchaseStatus.CONFIRM));

        log.debug("생성된 주문 내역 : {}", purchase);
    }

}