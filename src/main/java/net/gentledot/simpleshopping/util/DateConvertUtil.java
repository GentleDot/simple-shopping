package net.gentledot.simpleshopping.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class DateConvertUtil {
    public static LocalDate convertStringToLocalDate(String dateStr) {
        LocalDate date;

        // TODO 적절한 Exception 설정 필요
        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", dateStr)) {
            date = LocalDate.parse(dateStr);
        } else if (Pattern.matches("\\d{8}", dateStr)) {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } else if (Pattern.matches("\\d{4}/\\d{2}/\\d{2}", dateStr)) {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        } else if (Pattern.matches("\\d{4}.\\d{2}.\\d{2}", dateStr)) {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        } else {
            throw new RuntimeException("유효한 날짜 형식이 아닙니다.");
        }

        return date;
    }
}
