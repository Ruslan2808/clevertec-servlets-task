package ru.clevertec.service;

import ru.clevertec.dto.product.ProductRequest;
import ru.clevertec.dto.product.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAll(Integer pageSize, Integer pageNumber);
    ProductResponse findById(Long id);
    void save(ProductRequest productRequest);
    void update(Long id, ProductRequest productRequest);
    void deleteById(Long id);
}
