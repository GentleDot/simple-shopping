package net.gentledot.simpleshopping.models.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    @DisplayName("이메일 주소로 객체 생성")
    void createEmailTest() {
        String address = "test1@test.com";
        Email email = new Email(address);

        assertThat(email, is(notNullValue()));
        assertThat(email.getAddress(), is(address));
        assertThat(email.getDomain(), is("test.com"));
        assertThat(email.getName(), is("test1"));
    }

    @Test
    @DisplayName("이메일 주소 형식에 맞지 않으면 IllegalArgumentException 발생")
    void createEmailTestWithNotEmailAddress() {
        String address = "test문자";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class
                , () -> new Email(address));

        assertThat(exception, is(notNullValue()));
        assertThat(exception.getMessage(), is("형식에 맞지 않는 이메일 주소입니다."));
    }
}