package net.gentledot.simpleshopping.models.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

import static net.gentledot.simpleshopping.common.util.DateConvertUtil.convertStringToLocalDate;

public class BookRequest {
    private String category;
    private String name;
    private String description;
    private String date;

    protected BookRequest() {
    }

    public String getCategory() {
        return category.toUpperCase();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return convertStringToLocalDate(date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("category", category)
                .append("name", name)
                .append("description", description)
                .append("date", date)
                .toString();
    }
}
