package ru.clevertec.parser;

import ru.clevertec.exception.InvalidParamException;
import ru.clevertec.util.ControllerUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UrlParamsParser {

    private static final Integer DEFAULT_PAGE_SIZE = 20;
    private static final Integer DEFAULT_PAGE_NUMBER = 1;
    private static final Integer DEFAULT_DISCOUNT_CARD_NUMBER = 0;
    private static final String NUMERIC_PARAMETER_NOT_VALID_EXCEPTION_MESSAGE = "Passed numeric parameter is not valid";
    private static final String PAGE_NUMBER_ZERO_EXCEPTION_MESSAGE = "Page number can't be zero";

    public static Map<String, Integer> parsePaginationParams(String pageSizeParam, String pageNumberParam) {
        Map<String, Integer> paginationParams = new HashMap<>();
        Integer pageSize = DEFAULT_PAGE_SIZE;
        Integer pageNumber = DEFAULT_PAGE_NUMBER;

        if (!Objects.isNull(pageSizeParam)) {
            pageSize = parseNumericUrlParam(pageSizeParam).intValue();
        }
        if (!Objects.isNull(pageNumberParam)) {
            pageNumber = parseNumericUrlParam(pageNumberParam).intValue();
        }
        if (pageNumber == 0) {
            throw new InvalidParamException(PAGE_NUMBER_ZERO_EXCEPTION_MESSAGE);
        }

        paginationParams.put(ControllerUtil.PAGE_SIZE_PARAM, pageSize);
        paginationParams.put(ControllerUtil.PAGE_NUMBER_PARAM, pageNumber);

        return paginationParams;
    }

    public static Long parseIdParam(String path) {
        String idParam = path.split("/")[1];

        return parseNumericUrlParam(idParam);
    }

    public static Map<Integer, Integer> parseProductParams(Map<String, String[]> receiptParams) {
        String[] productIdParams = receiptParams.get(ControllerUtil.PRODUCT_ID_PARAM);
        String[] productQtyParams = receiptParams.get(ControllerUtil.PRODUCT_QTY_PARAM);
        List<Integer> productsId = new ArrayList<>();
        List<Integer> productsQty = new ArrayList<>();

        if (!Objects.isNull(productIdParams) && !Objects.isNull(productQtyParams)) {
            productsId = Arrays.stream(productIdParams)
                    .map(UrlParamsParser::parseNumericUrlParam)
                    .map(Long::intValue)
                    .filter(productId -> productId != 0)
                    .toList();

            productsQty = Arrays.stream(productQtyParams)
                    .map(UrlParamsParser::parseNumericUrlParam)
                    .map(Long::intValue)
                    .filter(productId -> productId != 0)
                    .toList();
        }

        Map<Integer, Integer> products = new LinkedHashMap<>();

        Iterator<Integer> productsIdIterator = productsId.iterator();
        Iterator<Integer> productsQtyIterator = productsQty.iterator();

        while (productsIdIterator.hasNext() && productsQtyIterator.hasNext()) {
            Integer productId = productsIdIterator.next();
            Integer productQty = productsQtyIterator.next();

            if (products.containsKey(productId)) {
                productQty += products.get(productId);
            }

            products.put(productId, productQty);
        }

        return products;
    }

    public static Integer parseDiscountCardNumberParam(Map<String, String[]> receiptParams) {
        String[] discountCardNumberParams = receiptParams.get(ControllerUtil.DISCOUNT_CARD_NUMBER_PARAM);
        Integer discountCardNumber = DEFAULT_DISCOUNT_CARD_NUMBER;

        if (!Objects.isNull(discountCardNumberParams)) {
            String firstDiscountCardNumberParam = discountCardNumberParams[0];
            discountCardNumber = parseNumericUrlParam(firstDiscountCardNumberParam).intValue();
        }

        return discountCardNumber;
    }

    public static Long parseNumericUrlParam(String urlParam) {
        if (!StringUtils.isNumeric(urlParam)) {
            throw new InvalidParamException(NUMERIC_PARAMETER_NOT_VALID_EXCEPTION_MESSAGE);
        }

        return Long.parseLong(urlParam);
    }
}
