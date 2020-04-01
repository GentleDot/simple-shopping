package net.gentledot.simpleshopping.error;

public class MemberNotFoundException extends SimpleNotFoundException {
    private static final String TARGET_NAME = "Member";

    public MemberNotFoundException(String targetId) {
        super(TARGET_NAME, targetId);
    }
}