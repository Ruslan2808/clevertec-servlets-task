package ru.clevertec.controller;

import ru.clevertec.dto.discount_card.DiscountCardRequest;
import ru.clevertec.dto.discount_card.DiscountCardResponse;
import ru.clevertec.parser.UrlParamsParser;
import ru.clevertec.service.DiscountCardService;
import ru.clevertec.service.impl.DiscountCardServiceImpl;
import ru.clevertec.util.ControllerUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@WebServlet("/api/v1/discount-cards/*")
public class DiscountCardController extends HttpServlet {

    private DiscountCardService discountCardService;

    @Override
    public void init() {
        discountCardService = new DiscountCardServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();

        if (Objects.isNull(path)) {
            doGetAll(request, response);
            return;
        }

        doGetById(response, path);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();

        if (Objects.isNull(path)) {
            DiscountCardRequest discountCardRequest = ControllerUtil.requestBodyToEntity(DiscountCardRequest.class, request);

            discountCardService.save(discountCardRequest);

            response.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = UrlParamsParser.parseIdParam(request.getPathInfo());

        DiscountCardRequest discountCardRequest = ControllerUtil.requestBodyToEntity(DiscountCardRequest.class, request);
        discountCardService.update(id, discountCardRequest);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        Long id = UrlParamsParser.parseIdParam(request.getPathInfo());

        discountCardService.deleteById(id);

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void doGetAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Integer> paginationParams = ControllerUtil.getPaginationParams(request);
        Integer pageSize = paginationParams.get(ControllerUtil.PAGE_SIZE_PARAM);
        Integer pageNumber = paginationParams.get(ControllerUtil.PAGE_NUMBER_PARAM);

        List<DiscountCardResponse> discountCardResponses = discountCardService.findAll(pageSize, pageNumber);

        ControllerUtil.printJsonResponse(discountCardResponses, response, HttpServletResponse.SC_OK);
    }

    private void doGetById(HttpServletResponse response, String path) throws IOException {
        Long id = UrlParamsParser.parseIdParam(path);

        DiscountCardResponse discountCardResponse = discountCardService.findById(id);

        ControllerUtil.printJsonResponse(discountCardResponse, response, HttpServletResponse.SC_OK);
    }
}
