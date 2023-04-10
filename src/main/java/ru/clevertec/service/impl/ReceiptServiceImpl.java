package ru.clevertec.service.impl;

import ru.clevertec.entity.DiscountCard;
import ru.clevertec.entity.Product;
import ru.clevertec.entity.Receipt;
import ru.clevertec.entity.ReceiptProduct;
import ru.clevertec.exception.DiscountCardNotFoundException;
import ru.clevertec.exception.ProductNotFoundException;
import ru.clevertec.repository.impl.DiscountCardRepository;
import ru.clevertec.repository.impl.ProductRepository;
import ru.clevertec.service.ReceiptService;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiptServiceImpl implements ReceiptService {

    private static final String PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE = "Product with id = %d not found";
    private static final String DISCOUNT_CARD_NOT_FOUND_EXCEPTION_MESSAGE = "Discount card with id = %d not found";
    private static final String PRODUCTS_EMPTY_EXCEPTION_MESSAGE =
            "It's impossible to generate a receipt because the list of products is empty";

    private final ProductRepository productRepository = new ProductRepository();
    private final DiscountCardRepository discountCardRepository = new DiscountCardRepository();

    @Override
    public Receipt createReceipt(Map<Integer, Integer> products, Integer discountCardNumber) {
        List<ReceiptProduct> receiptProducts = new ArrayList<>();
        double discount = 0.0;

        if (products.isEmpty()) {
            throw new ProductNotFoundException(PRODUCTS_EMPTY_EXCEPTION_MESSAGE);
        }

        for (Integer productId : products.keySet()) {
            Product product = productRepository.findById(Long.valueOf(productId))
                    .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE, productId)));

            ReceiptProduct receiptProduct = ReceiptProduct.builder()
                    .qty(products.get(productId))
                    .name(product.getName())
                    .isPromotional(product.getIsPromotional())
                    .price(product.getPrice())
                    .build();

            receiptProducts.add(receiptProduct);
        }

        if (discountCardNumber != 0) {
            DiscountCard discountCard = discountCardRepository.findByNumber(discountCardNumber)
                    .orElseThrow(() -> new DiscountCardNotFoundException(String.format(DISCOUNT_CARD_NOT_FOUND_EXCEPTION_MESSAGE, discountCardNumber)));

            discount = discountCard.getDiscount();
        }

        return Receipt.builder()
                .creationDate(LocalDate.now())
                .creationTime(LocalTime.now())
                .receiptProducts(receiptProducts)
                .discount(discount)
                .build();
    }
}
