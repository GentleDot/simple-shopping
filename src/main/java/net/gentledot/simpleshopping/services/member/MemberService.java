package net.gentledot.simpleshopping.services.member;

import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.member.Member;
import net.gentledot.simpleshopping.models.request.MemberRequest;
import net.gentledot.simpleshopping.repositories.member.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean checkDuplicatedEmail(Email email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    public Member join(Email email, String password, String name) {
        return memberRepository.save(new Member(email, password, name));
    }

    // TODO 알맞는 Exception으로 변경 필요
    public Member myInfo(Email email, String password) {
        Member member = memberRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        if (!member.checkPassword(password)) {
            throw new RuntimeException("확인되지 않은 접근. (비밀번호 불일치)");
        }

        return member;
    }

    // TODO Login 구현 필요
}
