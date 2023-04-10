package ru.clevertec.mapper;

import ru.clevertec.entity.Receipt;

public interface ReceiptMapper<T> {
    T map(Receipt receipt);
}
