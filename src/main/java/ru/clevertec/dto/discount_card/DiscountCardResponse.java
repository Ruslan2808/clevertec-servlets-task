package ru.clevertec.dto.discount_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCardResponse {

    private Long id;
    private Integer number;
    private Double discount;

}
