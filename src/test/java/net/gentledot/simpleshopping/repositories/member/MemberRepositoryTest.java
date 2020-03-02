package net.gentledot.simpleshopping.repositories.member;

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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class MemberRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(MemberRepositoryTest.class);

    @Autowired
    MemberRepository memberRepository;

    @BeforeAll
    @DisplayName("테스트를 위한 member 저장 실행")
    void setUp() {
        Email email = new Email("test123@gmail.com");
        String password = "PROTECTED";
        String name = "testName";
        Member newMember = getTestMember(email, password, name);

        memberRepository.save(newMember);
    }

    @Test
    @DisplayName("member 객체 저장")
    void saveTest() {
        Email email = new Email("test01@gmail.com");
        String password = "PROTECTED";
        String name = "myName";
        Member newMember = getTestMember(email, password, name);

        Member savedMember = memberRepository.save(newMember);

        assertThat(savedMember, is(notNullValue()));
        assertThat(savedMember.getEmail(), is(email.getAddress()));
        assertThat(savedMember.getPassword(), is(password));
        assertThat(savedMember.getName().get(), is(name));
        assertThat(savedMember.getLastLoginAt(), is(nullValue()));

        log.debug("생성일자 : {}", savedMember.getCreateAt());
    }

    @Test
    @DisplayName("저장된 member를 email로 조회 가능")
    void findByEmailTest() {
        Email email = new Email("test123@gmail.com");
        String password = "PROTECTED";
        String name = "testName";

        Optional<Member> byEmail = memberRepository.findByEmail(email);

        assertThat(byEmail.isPresent(), is(true));
        assertThat(byEmail.get().getEmail(), is(email.getAddress()));
        assertThat(byEmail.get().getPassword(), is(password));
        assertThat(byEmail.get().getName().get(), is(name));

        log.debug("생성일자 : {}", byEmail.get().getCreateAt());
    }

    @Test
    @DisplayName("저장된 member 객체를 update")
    void update() {
        LocalDateTime now = LocalDateTime.now();
        log.debug("현재 시각 : {}", now);

        Email email = new Email("test123@gmail.com");
        Optional<Member> byEmail = memberRepository.findByEmail(email);

        String beforeName = byEmail.get().getName().orElse(null);
        log.debug("변경 전 이름 : {}", beforeName);

        Member changedMember = byEmail.map(member -> new Member.Builder(member)
                .name("changedName")
                .lastLoginAt(now)
                .build()).orElse(null);

        memberRepository.update(changedMember);

        Member afterChange = memberRepository.getOne(changedMember.getSeq());

        assertThat(afterChange.getEmail(), is(email.getAddress()));
        assertThat(afterChange.getName().get().equals(beforeName), is(false));

        log.debug("변경된 memeber : {}", afterChange.toString());
        log.debug("생성일자 : {}", afterChange.getCreateAt());
        log.debug("로그인 일자 : {}", afterChange.getLastLoginAt());
    }

    private Member getTestMember(Email email, String password, String name) {
        return new Member.Builder(email, password)
                .name(name)
                .build();
    }
}