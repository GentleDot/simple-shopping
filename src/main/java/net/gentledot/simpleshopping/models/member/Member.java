package net.gentledot.simpleshopping.models.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.gentledot.simpleshopping.common.util.argumentHandleUtil.checkExpression;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Entity(name = "member")
@Table(name = "members")
public class Member {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password", length = 200, nullable = false)
    private String password;

    @Column(name = "name", length = 30)
    private String name;

    @JsonIgnore
    @Column(name = "role", length = 100)
    private String role;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    protected Member() {
    }

    public Member(Email email, String password, String name) {
        this(null, email, password, name, new String[]{Role.user.getRoleValue()}, null, null);
    }

    public Member(Long seq, Email email, String password, String name, String[] role, LocalDateTime lastLoginAt, LocalDateTime createAt) {
        checkExpression(isNotEmpty(email), "이메일은 반드시 존재해야 합니다.");
        checkExpression(StringUtils.isNotBlank(password), "비밀번호는 빈 값이 될 수 없습니다.");
        checkExpression(password.length() <= 100, "비밀번호는 200자 이내로 입력 가능합니다.");
        if (StringUtils.isNotBlank(name)) {
            checkExpression(name.getBytes(StandardCharsets.UTF_8).length <= 30, "이름은 30bytes 를 넘을 수 없습니다.");
        }

        this.seq = seq == null ? 0 : seq;
        this.email = email.getAddress();
        this.password = password;
        this.name = name;
        this.role = role == null ? null : String.join(",", role);
        this.lastLoginAt = lastLoginAt;
        this.createAt = defaultIfNull(createAt, LocalDateTime.now());
    }

    public long getSeq() {
        return seq;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getRole() {
        return Optional.ofNullable(role);
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void afterLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void checkPassword(PasswordEncoder encoder, String credentialPassword) {
        if (!encoder.matches(credentialPassword, password)) {
            throw new AuthenticationServiceException("입력된 패스워드가 올바르지 않습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(seq, member.seq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seq);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("seq", seq)
                .append("email", email)
                .append("password", password)
                .append("name", name)
                .append("role", role)
                .append("lastLoginAt", lastLoginAt)
                .append("createAt", createAt)
                .toString();
    }

    public static final class Builder {
        private long seq;
        private Email email;
        private String password;
        private String name;
        private String[] role;
        private LocalDateTime lastLoginAt;
        private LocalDateTime createAt;

        public Builder(Email email, String password) {
            this.email = email;
            this.password = password;
        }

        public Builder(Member member) {
            this.seq = member.seq;
            this.email = new Email(member.email);
            this.password = member.password;
            this.name = member.name;
            this.lastLoginAt = member.lastLoginAt;
            this.createAt = member.createAt;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder role(String[] role) {
            this.role = role;
            return this;
        }

        public Builder lastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public Member build() {
            return new Member(seq, email, password, name, role, lastLoginAt, createAt);
        }
    }
}
