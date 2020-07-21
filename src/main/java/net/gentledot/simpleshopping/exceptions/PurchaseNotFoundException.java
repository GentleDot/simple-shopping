package net.gentledot.simpleshopping.exceptions;

public class PurchaseNotFoundException extends SimpleNotFoundException {
    private static final String TARGET_NAME = "주문 내역";

    public PurchaseNotFoundException(Long targetId) {
        super(TARGET_NAME, targetId);
    }
}