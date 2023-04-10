package ru.clevertec.mapper;

import ru.clevertec.dto.discount_card.DiscountCardRequest;
import ru.clevertec.dto.discount_card.DiscountCardResponse;
import ru.clevertec.entity.DiscountCard;

public class DiscountCardMapper {

    public static DiscountCardResponse mapToDiscountCardResponse(DiscountCard discountCard) {
        return DiscountCardResponse.builder()
                .id(discountCard.getId())
                .number(discountCard.getNumber())
                .discount(discountCard.getDiscount())
                .build();
    }

    public static DiscountCard mapToDiscountCard(DiscountCardRequest discountCardRequest) {
        return DiscountCard.builder()
                .number(discountCardRequest.getNumber())
                .discount(discountCardRequest.getDiscount())
                .build();
    }
}
