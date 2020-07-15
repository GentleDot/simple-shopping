package net.gentledot.simpleshopping.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.member.Member;
import net.gentledot.simpleshopping.models.member.MemberAccount;
import net.gentledot.simpleshopping.repositories.member.MemberRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final JwtProperties jwtProperties;
    private final MemberRepository memberRepository;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtProperties jwtProperties, MemberRepository memberRepository) {
        super(authenticationManager);
        this.jwtProperties = jwtProperties;
        this.memberRepository = memberRepository;
    }

    // 인증 확인
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // request header에서 Bearer JWT Token 확인
        String header = request.getHeader(jwtProperties.getHeaderName());

        // header가 없거나 header 내 Bearer Token이 존재하지 않는 경우 filter 통과
        if (header == null || !header.startsWith(jwtProperties.getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        log.debug("JWT 확인 : {}", header);
        // Header의 JWT를 통해 user email을 확인하고 인증 처리.
        Authentication authentication = getUsernamePasswordAuthentication(header);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue filter execution
        chain.doFilter(request, response);
    }

    private Authentication getUsernamePasswordAuthentication(String requestHeader) {
        if (requestHeader != null) {
            // parse the token and validate it (decode)
            String username = JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey().getBytes()))
                    .build()
                    .verify(requestHeader.replace(jwtProperties.getPrefix(), ""))
                    .getSubject();

            // DB에서 username의 Member를 확인
            // Member의 로그인 시간 update, 인증 토큰(UsernamePasswordAuthenticationToken) 발급
            if (StringUtils.isNotBlank(requestHeader)) {
                Email email = new Email(username);
                Member member = memberRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException(String.format("해당 ID(%s)의 Member가 존재하지 않습니다.", email.getAddress())));

                log.debug("확인된 member : {}", member);

                member.afterLogin();
                memberRepository.update(member);
                log.debug("로그인 시각 : {}", member.getLastLoginAt());

                MemberAccount account = new MemberAccount(member);
                return new UsernamePasswordAuthenticationToken(username, null, account.getAuthorities());
            }

            return null;
        }
        return null;
    }

}
