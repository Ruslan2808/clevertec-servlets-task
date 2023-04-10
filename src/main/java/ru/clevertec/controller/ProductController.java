package ru.clevertec.controller;

import ru.clevertec.dto.product.ProductRequest;
import ru.clevertec.dto.product.ProductResponse;
import ru.clevertec.parser.UrlParamsParser;
import ru.clevertec.service.ProductService;
import ru.clevertec.service.impl.ProductServiceImpl;
import ru.clevertec.util.ControllerUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@WebServlet(value = "/api/v1/products/*")
public class ProductController extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {
        this.productService = new ProductServiceImpl();
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
            ProductRequest productRequest = ControllerUtil.requestBodyToEntity(ProductRequest.class, request);

            productService.save(productRequest);

            response.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = UrlParamsParser.parseIdParam(request.getPathInfo());

        ProductRequest productRequest = ControllerUtil.requestBodyToEntity(ProductRequest.class, request);
        productService.update(id, productRequest);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        Long id = UrlParamsParser.parseIdParam(request.getPathInfo());

        productService.deleteById(id);

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void doGetAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Integer> paginationParams = ControllerUtil.getPaginationParams(request);
        Integer pageSize = paginationParams.get(ControllerUtil.PAGE_SIZE_PARAM);
        Integer pageNumber = paginationParams.get(ControllerUtil.PAGE_NUMBER_PARAM);

        List<ProductResponse> productResponses = productService.findAll(pageSize, pageNumber);

        ControllerUtil.printJsonResponse(productResponses, response, HttpServletResponse.SC_OK);
    }

    private void doGetById(HttpServletResponse response, String path) throws IOException {
        Long id = UrlParamsParser.parseIdParam(path);

        ProductResponse productResponse = productService.findById(id);

        ControllerUtil.printJsonResponse(productResponse, response, HttpServletResponse.SC_OK);
    }
}
