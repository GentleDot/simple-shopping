package net.gentledot.simpleshopping.models.response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.HttpStatus;

public class ApiResult<T> {
    private final boolean processSuccess;
    private final T response;
    private final ApiError error;

    public ApiResult(boolean processSuccess, T response, ApiError error) {
        this.processSuccess = processSuccess;
        this.response = response;
        this.error = error;
    }

    public boolean isProcessSuccess() {
        return processSuccess;
    }

    public T getResponse() {
        return response;
    }

    public ApiError getError() {
        return error;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("processSuccess", processSuccess)
                .append("response", response)
                .append("error", error)
                .toString();
    }

    public static <T> ApiResult<T> ok(T response) {
        return new ApiResult<>(true, response, null);
    }

    public static ApiResult error(String errorMessage, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(errorMessage, status));
    }
}
