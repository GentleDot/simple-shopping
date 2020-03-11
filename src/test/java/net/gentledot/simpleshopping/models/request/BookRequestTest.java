package net.gentledot.simpleshopping.models.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


class BookRequestTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    @DisplayName("전달받은 날짜 문자열을 LocalDate 타입으로 변환")
    void convertToLocalDateTest(){
        LocalDate parseDefault = LocalDate.parse("2019-01-01");

        log.debug("날짜 확인 : {}", parseDefault);
        log.debug("날짜 확인 : {}", parseDefault.getYear());
        log.debug("날짜 확인 : {}", parseDefault.getDayOfMonth());

        LocalDate parseWithFormatter = LocalDate.parse("20190101", DateTimeFormatter.ofPattern("yyyyMMdd"));
        log.debug("날짜 확인 : {}", parseWithFormatter);
        log.debug("날짜 확인 : {}", parseWithFormatter.getYear());
        log.debug("날짜 확인 : {}", parseWithFormatter.getDayOfMonth());


        LocalDate parseWithFormatterSlash = LocalDate.parse("2019/01/01", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        log.debug("날짜 확인 : {}", parseWithFormatterSlash);
        log.debug("날짜 확인 : {}", parseWithFormatterSlash.getYear());
        log.debug("날짜 확인 : {}", parseWithFormatterSlash.getDayOfMonth());

        LocalDate parseWithFormatterDot = LocalDate.parse("2019.01.01", DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        log.debug("날짜 확인 : {}", parseWithFormatterDot);
        log.debug("날짜 확인 : {}", parseWithFormatterDot.getYear());
        log.debug("날짜 확인 : {}", parseWithFormatterDot.getDayOfMonth());
    }
}