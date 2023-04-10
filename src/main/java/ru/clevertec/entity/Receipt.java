package ru.clevertec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    private LocalDate creationDate;
    private LocalTime creationTime;
    private List<ReceiptProduct> receiptProducts;
    private Double discount;

    public Double calculateUnDiscountedTotal() {
        return receiptProducts.stream()
                .map(ReceiptProduct::calculateTotal)
                .reduce(Double::sum)
                .orElse(0.0);
    }

    public Double calculateTotal() {
        return Math.round(calculateUnDiscountedTotal() * (1 - this.discount / 100.0) * 100.0) / 100.0;
    }
}
