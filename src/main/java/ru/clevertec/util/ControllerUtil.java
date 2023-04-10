package ru.clevertec.util;

import ru.clevertec.parser.UrlParamsParser;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;
import java.util.stream.Collectors;

public class ControllerUtil {

    private static final Gson gson = new Gson();
    public static final String PAGE_SIZE_PARAM = "pageSize";
    public static final String PAGE_NUMBER_PARAM = "pageNumber";
    public static final String PRODUCT_ID_PARAM = "productId";
    public static final String PRODUCT_QTY_PARAM = "productQty";
    public static final String DISCOUNT_CARD_NUMBER_PARAM = "card";

    public static void printJsonResponse(Object obj, HttpServletResponse response, int statusCode) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String jsonResponse = gson.toJson(obj);

        out.println(jsonResponse);
        out.flush();

        response.setStatus(statusCode);
    }

    public static <T> T requestBodyToEntity(Class<T> classType, HttpServletRequest request) throws IOException {
        String requestBody = request.getReader()
                .lines()
                .collect(Collectors.joining());

        return gson.fromJson(requestBody, classType);
    }

    public static Map<String, Integer> getPaginationParams(HttpServletRequest request) {
        String pageSizeParam = request.getParameter(PAGE_SIZE_PARAM);
        String pageNumberParam = request.getParameter(PAGE_NUMBER_PARAM);

        return UrlParamsParser.parsePaginationParams(pageSizeParam, pageNumberParam);
    }
}
