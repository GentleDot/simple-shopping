package net.gentledot.simpleshopping.models.book;

import java.util.Arrays;

public enum BookCategory {
    DRAMA("dra"), HUMOR("hum"), TEXTBOOK("txb"), ESSAY("ess");

    private final String code;

    BookCategory(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static BookCategory getCategory(String code) {
        return Arrays.stream(BookCategory.values())
                .filter(bookCategory -> bookCategory.code.equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("해당 코드 (%s) 에 해당하는 category는 존재하지 않습니다.", code)));
    }
}
