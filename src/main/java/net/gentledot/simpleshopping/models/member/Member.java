package net.gentledot.simpleshopping.models.member;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static net.gentledot.simpleshopping.util.checkArgumentUtil.checkExpression;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

public class Member {
    private final Long seq;
    private final String email;
    private final String password;
    private String name;
    private LocalDateTime lastLoginAt;
    private final LocalDateTime createAt;

    public Member(Email email, String password, String name) {
        this(null, email, password, name, null, null);
    }

    public Member(Long seq, Email email, String password, String name, LocalDateTime lastLoginAt, LocalDateTime createAt) {
        checkExpression(isNotEmpty(email), "이메일은 반드시 존재해야 합니다.");
        checkExpression(isNotEmpty(password), "비밀번호는 반드시 존재해야 합니다.");

        this.seq = seq;
        this.email = email.getAddress();
        this.password = password;
        this.name = name;
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
                .append("lastLoginAt", lastLoginAt)
                .append("createAt", createAt)
                .toString();
    }

    public static final class Builder {
        private long seq;
        private Email email;
        private String password;
        private String name;
        private LocalDateTime lastLoginAt;
        private LocalDateTime createAt;

        public Builder() {
        }

        public Builder(Member member) {
            this.seq = member.seq;
            this.email = new Email(member.email);
            this.password = member.password;
            this.name = member.name;
            this.lastLoginAt = member.lastLoginAt;
            this.createAt = member.createAt;
        }

        public Builder seq(long seq) {
            this.seq = seq;
            return this;
        }

        public Builder email(Email email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder lastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public Builder createAt(LocalDateTime createAt) {
            this.createAt = createAt;
            return this;
        }

        public Member build() {
            return new Member(seq, email, password, name, lastLoginAt, createAt);
        }
    }
}
