package net.gentledot.simpleshopping.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.simpleshopping.models.response.ApiResult;
import net.gentledot.simpleshopping.repositories.member.MemberRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final MemberRepository memberRepository;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtProperties jwtProperties, MemberRepository memberRepository) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
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

        log.info("auth (JWT) 확인 : {}", header);
        // Header의 JWT를 통해 user email을 확인하고 인증 처리.
        Authentication authentication = getUsernamePasswordAuthentication(header, response);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue filter execution
        chain.doFilter(request, response);
    }

    private Authentication getUsernamePasswordAuthentication(String requestHeader, HttpServletResponse response) throws IOException {
        if (StringUtils.isNotBlank(requestHeader)) {
            // parse the token and validate it (decode)
            try {
                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey().getBytes()))
                        .build()
                        .verify(requestHeader.replace(jwtProperties.getPrefix(), ""));

                // username, role 확인
                String username = decodedJWT.getSubject();

                List<SimpleGrantedAuthority> rolesList = null;
                Claim roles = decodedJWT.getClaim("roles");
                if (roles != null) {
                    rolesList = roles.asList(SimpleGrantedAuthority.class);
                    log.info("role 확인 : {}", rolesList);
                }

                // 인증 토큰(UsernamePasswordAuthenticationToken) 발급
                if (StringUtils.isNotBlank(username)) {
                    return new UsernamePasswordAuthenticationToken(username, null, rolesList);
                }
            } catch (TokenExpiredException e) { // 토큰 만료 handling
                ObjectMapper objectMapper = new ObjectMapper();
                ApiResult expiredError = ApiResult.error("Authentication error : access token expired.", HttpStatus.UNAUTHORIZED);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("content-type", MediaType.APPLICATION_JSON_VALUE);
                ServletOutputStream outputStream = response.getOutputStream();

                objectMapper.writeValue(outputStream, expiredError);
                outputStream.flush();
                outputStream.close();
            }
        }
        return null;
    }
}
