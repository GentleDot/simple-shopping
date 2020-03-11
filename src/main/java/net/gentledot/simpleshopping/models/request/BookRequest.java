package net.gentledot.simpleshopping.models.request;

import java.time.LocalDate;

import static net.gentledot.simpleshopping.util.DateConvertUtil.convertStringToLocalDate;

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
}
