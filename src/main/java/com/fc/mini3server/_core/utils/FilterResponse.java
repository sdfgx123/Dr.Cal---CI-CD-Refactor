package com.fc.mini3server._core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.mini3server._core.handler.exception.Exception401;
import com.fc.mini3server._core.handler.exception.Exception403;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@NoArgsConstructor
public class FilterResponse {
    private static final ObjectMapper OBJECT_MAPPER;
    private static final String CONTENT_TYPE;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        CONTENT_TYPE = "application/json; charset=utf-8";
    }

    public static void unAuthorized(HttpServletResponse response, Exception401 exception) throws IOException {
        response.setStatus(exception.status().value());
        response.setContentType(CONTENT_TYPE);

        ApiUtils.ApiResult<?> errorResponse = ApiUtils.error(exception.getMessage(), exception.status());
        String responseBody = OBJECT_MAPPER.writeValueAsString(errorResponse);
        response.getWriter().println(responseBody);
    }

    public static void forbidden(HttpServletResponse response, Exception403 exception) throws IOException {
        response.setStatus(exception.status().value());
        response.setContentType(CONTENT_TYPE);

        ApiUtils.ApiResult<?> errorResponse = ApiUtils.error(exception.getMessage(), exception.status());
        String responseBody = OBJECT_MAPPER.writeValueAsString(errorResponse);
        response.getWriter().println(responseBody);
    }
}
