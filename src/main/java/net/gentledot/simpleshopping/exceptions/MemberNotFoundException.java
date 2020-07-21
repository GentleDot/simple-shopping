package net.gentledot.simpleshopping.exceptions;

public class MemberNotFoundException extends SimpleNotFoundException {
    private static final String TARGET_NAME = "Member";

    public MemberNotFoundException(String targetId) {
        super(TARGET_NAME, targetId);
    }
}