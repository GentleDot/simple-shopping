package net.gentledot.simpleshopping.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.member.Member;
import net.gentledot.simpleshopping.models.member.MemberAccount;
import net.gentledot.simpleshopping.models.request.LoginRequest;
import net.gentledot.simpleshopping.repositories.member.MemberRepository;
import net.gentledot.simpleshopping.services.member.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class JwtAttemptAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtAttemptAuthenticationFilter(AuthenticationManager authenticationManager, JwtProperties jwtProperties, MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtProperties = jwtProperties;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * POST api/v1/auth 요청 시 login Token을 발급
     * 형식은 {"username":"gentledot@email.com", "password":"pass"}
     * */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("요청 url : {}", request.getRequestURL());
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        LoginRequest credentials = null;

        try {
            log.debug("request 확인 : {}", request.getInputStream());
            credentials = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            log.debug("login request를 객체에 mapping 하지 못했음.", e);
        }

        String username = credentials.getUsername();
        Email email = new Email(username);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("해당 ID(%s)의 Member가 존재하지 않습니다.", email.getAddress())));

        log.debug("확인된 member : {}", member);

        member.checkPassword(passwordEncoder, credentials.getPassword());

        // login token 생성
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(),
                credentials.getPassword());

        // user 인증
        return authenticationManager.authenticate(authenticationToken);
    }

    /*
     * 인증이 성공하게 되면 JWT Token을 생성하여 response Header에 추가
     * */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // principal 확인
        MemberAccount principal = (MemberAccount) authResult.getPrincipal();
        log.debug("principal 확인 : {}", principal.getUsername());

        // 토큰 유효일자 설정
        LocalDate expriresAt = LocalDate.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() + jwtProperties.getExpireSeconds()), ZoneId.systemDefault());

        // Create JWT Token
        String token = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(Date.valueOf(expriresAt))
                .sign(Algorithm.HMAC512(jwtProperties.getSecretKey().getBytes()));

        log.debug("발행된 token : {}", token);

        // response Header에 추가
        response.addHeader(jwtProperties.getHeaderName(), jwtProperties.getPrefix() + token);
    }
}
