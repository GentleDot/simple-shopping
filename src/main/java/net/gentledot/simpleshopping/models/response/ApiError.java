package net.gentledot.simpleshopping.models.response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.HttpStatus;

public class ApiError {
    private final String errorMessage;
    private final int status;

    public ApiError(String errorMessage, HttpStatus status) {
        this.errorMessage = errorMessage;
        this.status = status.value();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatus getStatus() {
        return HttpStatus.valueOf(status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("errorMessage", errorMessage)
                .append("status", status)
                .toString();
    }
}
