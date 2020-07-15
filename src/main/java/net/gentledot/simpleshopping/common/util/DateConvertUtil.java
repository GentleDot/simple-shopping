package net.gentledot.simpleshopping.common.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class DateConvertUtil {
    public static LocalDate convertStringToLocalDate(String dateStr) {
        LocalDate date;

        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", dateStr)) {
            date = LocalDate.parse(dateStr);
        } else if (Pattern.matches("\\d{8}", dateStr)) {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } else if (Pattern.matches("\\d{4}/\\d{2}/\\d{2}", dateStr)) {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        } else if (Pattern.matches("\\d{4}.\\d{2}.\\d{2}", dateStr)) {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        } else {
            throw new DateTimeException(String.format("%s 는 유효한 날짜 형식이 아닙니다.", dateStr));
        }

        return date;
    }
}
