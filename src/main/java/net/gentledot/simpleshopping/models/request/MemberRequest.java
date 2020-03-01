package net.gentledot.simpleshopping.models.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MemberRequest {
    private String email;
    private String password;
    private String name;

    protected MemberRequest() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("email", email)
                .append("password", password)
                .append("name", name)
                .toString();
    }
}
