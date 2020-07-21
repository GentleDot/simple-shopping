package net.gentledot.simpleshopping.exceptions;

public class GoodsNotFoundException extends SimpleNotFoundException {
    private static final String TARGET_NAME = "상품";

    public GoodsNotFoundException(String targetId) {
        super(TARGET_NAME, targetId);
    }
}

