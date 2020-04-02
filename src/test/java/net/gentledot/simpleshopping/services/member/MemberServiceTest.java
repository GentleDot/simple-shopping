package net.gentledot.simpleshopping.services.member;

import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.member.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberServiceTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MemberService memberService;

    @BeforeAll
    @DisplayName("테스트를 위한 멤버 저장")
    void setUp() {
        Email email = new Email("testMail@test.com");
        String password = "PROTECTED";
        String name = "testName";

        memberService.join(email, password, name);
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void checkDuplicatedEmailTest() {
        Email email = new Email("testMail@test.com");
        boolean byEmail = memberService.checkDuplicatedEmail(email);

        assertThat(byEmail, is(true));
    }

    @Test
    @DisplayName("신규 회원 저장")
    void joinTest() {
        Email email = new Email("newMember@test.com");
        String password = "PROTECTED";
        String name = "welcomeNewMember";

        Member join = memberService.join(email, password, name);

        assertThat(join, is(notNullValue()));
        assertThat(join.getEmail(), is(email.getAddress()));
        assertThat(join.getPassword(), is(notNullValue()));
        assertThat(join.getName().get(), is(name));

        log.debug("저장된 member : {}", join);
    }

    @Test
    @DisplayName("회원 정보 조회")
    void myInfoTest() {
        String address = "testMail@test.com";
        Email email = new Email(address);

        Member member = memberService.myInfo(email, address);

        assertThat(member, is(notNullValue()));
        assertThat(member.getEmail(), is(email.getAddress()));

        log.debug("저장된 member : {}", member);
    }


    @Test
    @DisplayName("회원 정보 조회 시 로그인 유저명과 email address가 동일하지 않으면 접근 불가")
    void myInfoTestWithNotOwnPassword() {
        String address = "wrongEmailAdress@test.com";
        Email email = new Email("testMail@test.com");

        var exception = assertThrows(RuntimeException.class,
                () -> memberService.myInfo(email, address));

        assertThat(exception.getClass(), is(IllegalArgumentException.class));
        assertThat(exception.getMessage(), is("요청 email이 로그인 사용자 email과 일치하지 않습니다."));
    }
}