package net.gentledot.simpleshopping.models.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

public class BuyRequest {
    private Map<String, Integer> order;

    protected BuyRequest() {
    }

    public Map<String, Integer> getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("order", order)
                .toString();
    }
}
