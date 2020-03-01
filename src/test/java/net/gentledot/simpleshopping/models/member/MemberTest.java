package net.gentledot.simpleshopping.models.member;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberTest {

    @Test
    @DisplayName("Member 객체 생성")
    void createMemberTest() {
        String address = "test1@test.com";
        Email email = new Email(address);
        String password = "PROTECTED";

        Member newMember = new Member.Builder()
                .email(email)
                .password(password)
                .build();

        assertThat(newMember, is(notNullValue()));
        assertThat(newMember.getEmail(), is(address));
        assertThat(newMember.getPassword(), is(password));
        assertThat(newMember.getName(), is(Optional.empty()));
    }

    @Test
    @DisplayName("Member 객체는 필수입력 항목 없이 생성 불가능 (email, password)")
    void createMemberTestWithEmptyEmailOrPassword() {
        String address = "test1@test.com";
        Email email = new Email(address);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> new Member.Builder().email(email).build());

        assertThat(exception.getMessage(), is("비밀번호는 반드시 존재해야 합니다."));
    }
}