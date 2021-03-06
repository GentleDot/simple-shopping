package net.gentledot.simpleshopping.models.member;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static java.util.regex.Pattern.matches;
import static net.gentledot.simpleshopping.common.util.argumentHandleUtil.checkExpression;

public class Email {
    private final String address;

    public Email(String address) {

        checkExpression(StringUtils.isNotBlank(address), "이메일 주소는 반드시 존재해야 합니다.");
        checkExpression(address.length() < 50, "이메일 주소는 50자 이내로 입력 가능합니다.");
        checkExpression(checkAddress(address), "형식에 맞지 않는 이메일 주소입니다.");

        this.address = address;
    }

    public String getName() {
        String[] parts = address.split("@");
        if (parts.length == 2)
            return parts[0];
        return null;
    }

    public String getDomain() {
        String[] parts = address.split("@");
        if (parts.length == 2)
            return parts[1];
        return null;
    }

    public String getAddress() {
        return address;
    }

    private boolean checkAddress(String address) {
        return matches("([\\w~\\-.+]+)@([\\w~\\-]+)(\\.[\\w~\\-]+)+", address);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("address", address)
                .toString();
    }
}
