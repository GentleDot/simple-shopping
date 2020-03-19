package net.gentledot.simpleshopping.controllers.member;

import net.gentledot.simpleshopping.controllers.BaseController;
import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.member.Member;
import net.gentledot.simpleshopping.models.member.MemberAccount;
import net.gentledot.simpleshopping.models.request.MemberRequest;
import net.gentledot.simpleshopping.models.response.ApiResult;
import net.gentledot.simpleshopping.services.member.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class MemberController extends BaseController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping(path = "member/checkIsExistedEmail")
    public ApiResult<Boolean> checkEmailExisted(@RequestBody MemberRequest request) {
        String address = request.getEmail();
        return ApiResult.ok(memberService.checkDuplicatedEmail(new Email(address)));
    }

    @PostMapping(path = "member/join")
    public ApiResult<Member> join(@RequestBody MemberRequest request) {
        return ApiResult.ok(memberService.join(
                new Email(request.getEmail()),
                request.getPassword(),
                request.getName()));
    }

    @GetMapping(path = "member/myInfo")
    public ApiResult<Member> myInfo(@RequestBody MemberRequest request, Principal account) {
        log.debug("로그인 정보 확인 : {}", account);
        return ApiResult.ok(memberService.myInfo(
                new Email(request.getEmail()),
                account.getName()));
    }
}
