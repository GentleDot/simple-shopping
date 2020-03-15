package net.gentledot.simpleshopping.common.util;

import java.time.LocalDate;

public class argumentHandleUtil {
    public static void checkExpression(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static String getBookIdFromArguments(String categoryCode, LocalDate publishDate, String name) {
        StringBuffer stringBuffer = new StringBuffer();

        return stringBuffer
                .append(categoryCode)
                .append(publishDate.getYear())
                .append(String.format("%02d", publishDate.getMonthValue()))
                .append(name.replaceAll("\\s", ""))
                .toString();
    }
}
