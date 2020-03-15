package net.gentledot.simpleshopping.models.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class LoginRequest {
    private String username;
    private String password;

    protected LoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("username", username)
                .append("password", password)
                .toString();
    }
}
