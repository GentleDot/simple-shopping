package net.gentledot.simpleshopping.repositories.security;

import net.gentledot.simpleshopping.common.security.RefreshToken;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RefreshTokenRepositoryTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RefreshTokenRepository tokenRepository;

    @Test
    @DisplayName("RefeshToken 저장 테스트")
    @Order(1)
    void saveTest() {
        String username = "gentledot.wp@gmail.com";
        String refreshToken = "testRefresh";
        String authToken = "testAuth";
        RefreshToken token = new RefreshToken.Builder(username)
                .refreshToken(refreshToken)
                .authToken(authToken)
                .build();

        RefreshToken result = tokenRepository.save(token);

        assertThat(result.getUsername(), is(username));
        assertThat(result.getRefreshToken(), is(refreshToken));
        assertThat(result.getAuthToken(), is(authToken));
        assertThat(result.getCreatedDateTime(), is(notNullValue()));

        log.info("저장된 객체 : {}", result);
    }

    @Test
    @DisplayName("RefeshToken 조회 테스트")
    @Order(2)
    void findByUsername() {
        String username = "gentledot.wp@gmail.com";
        String refreshToken = "testRefresh";
        String authToken = "testAuth";

        saveTestToken(username, refreshToken, authToken);

        RefreshToken result = tokenRepository.findByUsername(username).orElse(null);

        assertThat(result.getUsername(), is(username));
        assertThat(result.getRefreshToken(), is(refreshToken));
        assertThat(result.getAuthToken(), is(authToken));
        assertThat(result.getCreatedDateTime(), is(notNullValue()));

        log.info("저장된 객체 : {}", result);
    }

    @Test
    @DisplayName("RefeshToken 수정 테스트")
    @Order(3)
    void update() {
        String username = "gentledot.wp@gmail.com";
        String refreshToken = "testRefresh";
        String authToken = "testAuth";

        RefreshToken targetToken = saveTestToken(username, refreshToken, authToken);
        LocalDateTime beforeCreatedDateTime = targetToken.getCreatedDateTime();
        RefreshToken token = new RefreshToken.Builder(targetToken)
                .authToken("newAuth")
                .refreshToken("newRefresh")
                .createdDateTime(LocalDateTime.now())
                .build();

        tokenRepository.update(token);

        RefreshToken result = tokenRepository.findByUsername(username).orElse(null);

        assertThat(result.getUsername().equals(username), is(true));
        assertThat(result.getUsername(), is(username));
        assertThat(result.getRefreshToken().equals(refreshToken), is(false));
        assertThat(result.getRefreshToken(), is("newRefresh"));
        assertThat(result.getAuthToken().equals(authToken), is(false));
        assertThat(result.getAuthToken(), is("newAuth"));
        assertThat(result.getCreatedDateTime().equals(beforeCreatedDateTime), is(false));

        log.info("저장된 객체 : {}", result);
    }

    @Test
    @DisplayName("RefeshToken 삭제 테스트")
    @Order(4)
    void deleteByUsername() {
        String username = "gentledot.wp@gmail.com";
        String refreshToken = "testRefresh";
        String authToken = "testAuth";

        RefreshToken targetToken = saveTestToken(username, refreshToken, authToken);

        tokenRepository.delete(targetToken);

        RefreshToken result = tokenRepository.findByUsername(username).orElse(null);

        assertThat(result, is(nullValue()));
    }

    private RefreshToken saveTestToken(String username, String refreshToken, String authToken) {
        RefreshToken token = new RefreshToken.Builder(username)
                .refreshToken(refreshToken)
                .authToken(authToken)
                .build();

        return tokenRepository.save(token);
    }
}