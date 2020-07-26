package net.gentledot.simpleshopping.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.member.Member;
import net.gentledot.simpleshopping.models.member.MemberAccount;
import net.gentledot.simpleshopping.models.request.LoginRequest;
import net.gentledot.simpleshopping.repositories.member.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

public class JwtAttemptAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${jwt.token.refresh.secret}")
    private String refreshSecret;
    @Value("${jwt.token.refresh.expiration_seconds}")
    private Long refreshExpireMillSeconds;

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
        log.info("요청 url : {}", request.getRequestURL());
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        LoginRequest credentials = null;

        try {
            log.info("request 확인 : {}", request.getInputStream());
            credentials = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            log.debug("login request를 객체에 mapping 하지 못했음.", e);
        }

        // member 확인
        String username = credentials.getUsername();
        Email email = new Email(username);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("해당 ID(%s)의 Member가 존재하지 않습니다.", email.getAddress())));

        log.info("확인된 member : {}", member);

        member.checkPassword(passwordEncoder, credentials.getPassword());

        // member로 확인되었다면 login 시각 기록
        member.afterLogin();
        memberRepository.update(member);
        log.info("로그인 시각 : {}", member.getLastLoginAt());

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
        log.info("principal의 username 확인 : {}", principal.getUsername());

        String[] memberRoles = principal.getAuthorities().stream()
                .map(Object::toString)
                .toArray(String[]::new);

        log.info("principal의 authorities 확인 : {}", Arrays.toString(memberRoles));

        // 토큰 유효일자 설정
        ZoneId systemZoneId = ZoneId.systemDefault();
        Date expireDate = new Date(System.currentTimeMillis() + jwtProperties.getExpireMillSeconds());
        log.info("expireDate : {}", expireDate);

        // Create JWT Token
        Date lastLoginDate = Date.from(principal.getMember().getLastLoginAt().atZone(systemZoneId).toInstant());
        String token = JWT.create()
                .withIssuedAt(lastLoginDate)
                .withExpiresAt(expireDate)
                .withSubject(principal.getUsername())
                .withArrayClaim("roles", memberRoles)
                .sign(Algorithm.HMAC512(jwtProperties.getSecretKey().getBytes()));

        /*// Create Refresh Token
        String refreshToken = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpireMillSeconds))
                .withClaim("username", principal.getUsername())
                .withClaim("loginDate",lastLoginDate)
                .sign(Algorithm.HMAC512(refreshSecret));*/


        // response Header에 추가
        response.addHeader(jwtProperties.getHeaderName(), jwtProperties.getPrefix() + token);
    }
}
