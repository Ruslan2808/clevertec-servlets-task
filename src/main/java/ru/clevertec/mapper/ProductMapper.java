package ru.clevertec.mapper;

import ru.clevertec.dto.product.ProductRequest;
import ru.clevertec.dto.product.ProductResponse;
import ru.clevertec.entity.Product;

public class ProductMapper {

    public static ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .isPromotional(product.getIsPromotional())
                .build();
    }

    public static Product mapToProduct(ProductRequest productRequest) {
        return Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .isPromotional(productRequest.getIsPromotional())
                .build();
    }
}
