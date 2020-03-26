package net.gentledot.simpleshopping.models.purchase;

import net.gentledot.simpleshopping.models.book.BookCategory;

import java.util.Arrays;

public enum PurchaseStatus {
    CONFIRM("conf"), CANCEL("canc");

    private String code;

    PurchaseStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PurchaseStatus getStatus(String code) {
        return Arrays.stream(PurchaseStatus.values())
                .filter(purchaseStatus -> purchaseStatus.code.equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("해당 코드 (%s) 에 해당하는 status는 존재하지 않습니다.", code)));
    }
}
