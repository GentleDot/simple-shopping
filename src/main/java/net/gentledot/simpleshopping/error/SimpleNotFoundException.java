package net.gentledot.simpleshopping.error;

public abstract class SimpleNotFoundException extends RuntimeException {
    public SimpleNotFoundException(String target, Object targetId) {
        super(setMessage(target, String.valueOf(targetId)));
    }

    private static String setMessage(String target, String targetId) {
        return String.format("해당 ID(%s)의 " + target + "이(가) 존재하지 않습니다.", targetId);
    }
}