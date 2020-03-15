package net.gentledot.simpleshopping.common.security;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JwtProperties {
    private final String secretKey;
    private final Long expireSeconds;
    private final String headerName;
    private final String prefix;

    public JwtProperties(String secretKey, Long expireSeconds, String headerName, String prefix) {
        this.secretKey = secretKey;
        this.expireSeconds = expireSeconds;
        this.headerName = headerName;
        this.prefix = prefix;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Long getExpireSeconds() {
        return expireSeconds;
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
                .append("expireSeconds", expireSeconds)
                .append("headerName", headerName)
                .append("prefix", prefix)
                .toString();
    }
}
