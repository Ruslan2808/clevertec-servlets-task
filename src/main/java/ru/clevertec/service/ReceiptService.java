package ru.clevertec.service;

import ru.clevertec.entity.Receipt;

import java.util.Map;

public interface ReceiptService {
    Receipt createReceipt(Map<Integer, Integer> products, Integer discountCardNumber);
}
