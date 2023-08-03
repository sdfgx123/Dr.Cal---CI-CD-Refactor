package com.fc.mini3server._core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public class ApiUtils {
    public static <T> ApiResult<T> success(T response){
        return new ApiResult<>(true, response, null);
    }

    public static <T> ApiPageResult<T> success(int totalPages, T response){
        return new ApiPageResult<>(true, totalPages, response, null);
    }

    public static ApiResult<?> error(String message, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(message, status.value()));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ApiResult<T> {
        private final boolean success;
        private final T item;
        private final ApiError error;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class ApiPageResult<T>{
        private final boolean success;
        private final int totalPages;
        private final T item;
        private final ApiError error;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class ApiError {
        private final String message;
        private final int status;
    }
}
