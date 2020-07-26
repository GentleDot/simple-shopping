package net.gentledot.simpleshopping.repositories.security;

import net.gentledot.simpleshopping.common.security.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByUsername(String username);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update refresh " +
            "    set " +
            "        refresh_token = :#{#refreshToken.refreshToken},\n" +
            "        auth_token = :#{#refreshToken.authToken},\n" +
            "        create_datetime = :#{#refreshToken.createdDateTime}\n" +
            "    where " +
            "        username = :#{#refreshToken.username}")
    void update(RefreshToken refreshToken);
}
