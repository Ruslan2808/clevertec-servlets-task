package ru.clevertec.service;

import ru.clevertec.dto.discount_card.DiscountCardRequest;
import ru.clevertec.dto.discount_card.DiscountCardResponse;

import java.util.List;

public interface DiscountCardService {
    List<DiscountCardResponse> findAll(Integer pageSize, Integer pageNumber);
    DiscountCardResponse findById(Long id);
    void save(DiscountCardRequest discountCardRequest);
    void update(Long id, DiscountCardRequest discountCardRequest);
    void deleteById(Long id);
}
