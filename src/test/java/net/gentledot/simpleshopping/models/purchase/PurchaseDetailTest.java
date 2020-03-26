package net.gentledot.simpleshopping.models.purchase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class PurchaseDetailTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    @DisplayName("주문 내역 객체 생성")
    void createPurchaseDetailTest() {
        String bookId = "ess201805testbook";
        PurchaseDetail purchaseDetail = new PurchaseDetail(bookId, 2);

        assertThat(purchaseDetail, is(notNullValue()));
        assertThat(purchaseDetail.getSeq(), is(0L));
        assertThat(purchaseDetail.getGoodsId(), is(bookId));
        assertThat(purchaseDetail.getQuantity(), is(2));

        log.debug("생성된 detail : {}", purchaseDetail);

    }
}