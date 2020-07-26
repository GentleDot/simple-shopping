package net.gentledot.simpleshopping.common.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static net.gentledot.simpleshopping.common.util.argumentHandleUtil.checkExpression;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Entity(name = "refresh")
@Table(name = "refresh_token_store")
public class RefreshToken {
    @Id
    private String username;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "auth_token")
    private String authToken;

    @Column(name = "create_datetime")
    private LocalDateTime createdDateTime;

    protected RefreshToken() {
    }

    public RefreshToken(String username, String refreshToken, String authToken, LocalDateTime createdDateTime) {
        checkExpression(StringUtils.isNotBlank(username), "member ID는 빈 값이 될 수 없습니다.");
        checkExpression(StringUtils.isNotBlank(refreshToken), "refreshToken 은 빈 값이 될 수 없습니다.");
        checkExpression(StringUtils.isNotBlank(authToken), "authToken 은 빈 값이 될 수 없습니다.");

        this.username = username;
        this.refreshToken = refreshToken;
        this.authToken = authToken;
        this.createdDateTime = defaultIfNull(createdDateTime, LocalDateTime.now());
    }

    public String getUsername() {
        return username;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RefreshToken that = (RefreshToken) o;

        return new EqualsBuilder()
                .append(username, that.username)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(username)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("username", username)
                .append("refreshToken", refreshToken)
                .append("authToken", authToken)
                .append("createdDateTime", createdDateTime)
                .toString();
    }

    public static final class Builder {
        private String username;
        private String refreshToken;
        private String authToken;
        private LocalDateTime createdDateTime;

        public Builder(String username) {
            this.username = username;
        }

        public Builder(RefreshToken token) {
            this.username = token.username;
            this.refreshToken = token.refreshToken;
            this.authToken = token.authToken;
            this.createdDateTime = token.createdDateTime;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder authToken(String authToken) {
            this.authToken = authToken;
            return this;
        }

        public Builder createdDateTime(LocalDateTime createdDateTime) {
            this.createdDateTime = createdDateTime;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(username, refreshToken, authToken, createdDateTime);
        }
    }
}


