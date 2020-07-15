package net.gentledot.simpleshopping.common.security;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JwtProperties {
    private final String secretKey;
    private final Long expireMillSeconds;
    private final String headerName;
    private final String prefix;

    public JwtProperties(String secretKey, Long expireMillSeconds, String headerName, String prefix) {
        this.secretKey = secretKey;
        this.expireMillSeconds = expireMillSeconds;
        this.headerName = headerName;
        this.prefix = prefix;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Long getExpireMillSeconds() {
        return expireMillSeconds;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("secretKey", secretKey)
                .append("expireSeconds", expireMillSeconds)
                .append("headerName", headerName)
                .append("prefix", prefix)
                .toString();
    }
}
