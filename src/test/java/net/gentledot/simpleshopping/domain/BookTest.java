package net.gentledot.simpleshopping.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class BookTest {

    @Test
    @DisplayName("@Builder로 Book entity가 생성되는지 확인.")
    void builderTest(){
        // Given
        Book book = Book.builder()
                .title("새로운 책")
                .description("책 생성 테스트")
                .build();

        // then
        assertThat(book, notNullValue());
    }
}