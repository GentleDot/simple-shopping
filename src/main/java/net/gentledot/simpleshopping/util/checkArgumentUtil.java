package net.gentledot.simpleshopping.util;

public class checkArgumentUtil {
    public static void checkExpression(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
