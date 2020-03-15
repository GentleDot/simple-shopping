package net.gentledot.simpleshopping.models.member;

public enum Role {
    user("USER");

    private String roleValue;

    Role(String roleValue) {
        this.roleValue = roleValue;
    }

    public String getRoleValue() {
        return roleValue;
    }
}
