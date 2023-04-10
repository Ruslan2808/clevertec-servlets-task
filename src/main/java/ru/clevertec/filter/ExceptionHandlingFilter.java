package ru.clevertec.filter;

import ru.clevertec.exception.ApiError;
import ru.clevertec.exception.DiscountCardAlreadyExistsException;
import ru.clevertec.exception.DiscountCardNotFoundException;
import ru.clevertec.exception.InvalidParamException;
import ru.clevertec.exception.ProductNotFoundException;
import ru.clevertec.util.ControllerUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebFilter(value = "/*")
public class ExceptionHandlingFilter implements Filter {

    private static final String TIMESTAMP_PATTERN = "dd/MM/yyyy HH:mm:ss";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            int statusCode = getResponseStatusCode(e);

            ApiError apiError = ApiError.builder()
                    .statusCode(statusCode)
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)))
                    .uri(httpRequest.getRequestURI())
                    .message(e.getMessage())
                    .build();

            httpResponse.setStatus(statusCode);
            ControllerUtil.printJsonResponse(apiError, httpResponse, statusCode);
        }
    }

    private int getResponseStatusCode(Exception e) {
        if (e instanceof ProductNotFoundException || e instanceof DiscountCardNotFoundException) {
            return HttpServletResponse.SC_NOT_FOUND;
        }
        if (e instanceof InvalidParamException || e instanceof DiscountCardAlreadyExistsException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        }

        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
