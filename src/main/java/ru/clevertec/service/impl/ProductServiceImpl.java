package ru.clevertec.service.impl;

import ru.clevertec.dto.product.ProductRequest;
import ru.clevertec.dto.product.ProductResponse;
import ru.clevertec.entity.Product;
import ru.clevertec.exception.ProductNotFoundException;
import ru.clevertec.mapper.ProductMapper;
import ru.clevertec.repository.CrudRepository;
import ru.clevertec.repository.impl.ProductRepository;
import ru.clevertec.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {

    private static final String NOT_FOUND_EXCEPTION_MESSAGE = "Product with id = %d not found";

    private final CrudRepository<Long, Product> productRepository = new ProductRepository();

    @Override
    public List<ProductResponse> findAll(Integer pageSize, Integer pageNumber) {
        List<Product> products = productRepository.findAll(pageSize, pageNumber);

        return products.stream()
                .map(ProductMapper::mapToProductResponse)
                .toList();
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format(NOT_FOUND_EXCEPTION_MESSAGE, id)));

        return ProductMapper.mapToProductResponse(product);
    }

    @Override
    public void save(ProductRequest productRequest) {
        Product product = ProductMapper.mapToProduct(productRequest);
        productRepository.save(product);
    }

    @Override
    public void update(Long id, ProductRequest productRequest) {
        Product product = ProductMapper.mapToProduct(productRequest);
        productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format(NOT_FOUND_EXCEPTION_MESSAGE, id)));

        productRepository.update(id, product);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format(NOT_FOUND_EXCEPTION_MESSAGE, id)));

        productRepository.deleteById(id);
    }
}
