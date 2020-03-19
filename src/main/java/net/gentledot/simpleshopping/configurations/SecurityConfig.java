package net.gentledot.simpleshopping.configurations;

import net.gentledot.simpleshopping.common.security.JwtAttemptAuthenticationFilter;
import net.gentledot.simpleshopping.common.security.JwtAuthenticationFilter;
import net.gentledot.simpleshopping.common.security.JwtProperties;
import net.gentledot.simpleshopping.repositories.member.MemberRepository;
import net.gentledot.simpleshopping.services.member.MemberDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberDetailsService memberDetailsService;
    private final MemberRepository memberRepository;

    @Value("${jwt.token.secret}")
    private String secretKey;
    @Value("${jwt.token.expiration_seconds}")
    private Long expireSeconds;
    @Value("${jwt.token.header}")
    private String headerName;
    @Value("${jwt.token.prefix}")
    private String prefix;

    public SecurityConfig(MemberDetailsService memberDetailsService, MemberRepository memberRepository) {
        this.memberDetailsService = memberDetailsService;
        this.memberRepository = memberRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(jwtAttemptAuthenticationFilter())
                .addFilter(jwtAuthenticationFilter())
                .requiresChannel()
                .antMatchers("/api/v1/**").requiresSecure()
                .antMatchers("/login*").requiresSecure()
                .and()
                .authorizeRequests()
                .antMatchers("/login*").permitAll()
                .antMatchers("/api/v1/member/join", "/api/v1/member/checkIsExistedEmail").permitAll()
                .antMatchers("/api/v1/book/**").hasRole("ADMIN")
                .anyRequest().authenticated();

//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.addFilterAfter(jwtAttemptAuthenticationFilter(), JwtAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(memberDetailsService);

        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    private JwtAttemptAuthenticationFilter jwtAttemptAuthenticationFilter() throws Exception {
        JwtProperties jwtProperties = new JwtProperties(secretKey, expireSeconds, headerName, prefix);
        return new JwtAttemptAuthenticationFilter(authenticationManager(), jwtProperties, memberRepository, passwordEncoder());
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtProperties jwtProperties = new JwtProperties(secretKey, expireSeconds, headerName, prefix);
        return new JwtAuthenticationFilter(authenticationManager(), jwtProperties, memberRepository);
    }

}
