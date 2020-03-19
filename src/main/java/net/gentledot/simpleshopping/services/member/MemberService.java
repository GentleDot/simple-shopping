package net.gentledot.simpleshopping.services.member;

import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.member.Member;
import net.gentledot.simpleshopping.repositories.member.MemberRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.gentledot.simpleshopping.common.util.argumentHandleUtil.checkExpression;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
public class MemberService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean checkDuplicatedEmail(Email email) {
        checkExpression(isNotEmpty(email), "이메일은 반드시 존재해야 합니다.");
        return memberRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public Member join(Email email, String password, String name) {
        checkExpression(isNotEmpty(email), "이메일은 반드시 존재해야 합니다.");
        checkExpression(StringUtils.isNotBlank(password), "비밀번호는 반드시 존재해야 합니다.");
        return memberRepository.save(new Member(email, passwordEncoder.encode(password), name));
    }

    // TODO 알맞는 Exception으로 변경 필요
    public Member myInfo(Email email, String username) {
        checkExpression(isNotEmpty(email), "이메일은 반드시 존재해야 합니다.");
        checkExpression(StringUtils.isNotBlank(username), "접속 유저명은 반드시 존재해야 합니다.");

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("해당 ID(%s)의 Member가 존재하지 않습니다.", email.getAddress())));
        if (!member.getEmail().equals(username)) {
            throw new RuntimeException("확인되지 않은 접근. (로그인 이메일과 불일치)");
        }
        return member;
    }

    @Transactional
    public Member updateLastLoginAt(Email email) {
        checkExpression(isNotEmpty(email), "이메일은 반드시 존재해야 합니다.");

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("해당 ID(%s)의 Member가 존재하지 않습니다.", email.getAddress())));

        member.afterLogin();
        memberRepository.update(member);
        log.debug("로그인 시각 : {}", member.getLastLoginAt());

        return member;
    }
}
