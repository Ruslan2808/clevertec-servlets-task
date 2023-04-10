package ru.clevertec.service.impl;

import ru.clevertec.dto.discount_card.DiscountCardRequest;
import ru.clevertec.dto.discount_card.DiscountCardResponse;
import ru.clevertec.entity.DiscountCard;
import ru.clevertec.exception.DiscountCardAlreadyExistsException;
import ru.clevertec.exception.DiscountCardNotFoundException;
import ru.clevertec.exception.ProductNotFoundException;
import ru.clevertec.mapper.DiscountCardMapper;
import ru.clevertec.repository.Repository;
import ru.clevertec.repository.impl.DiscountCardRepository;
import ru.clevertec.service.DiscountCardService;

import java.util.List;

public class DiscountCardServiceImpl implements DiscountCardService {

    private static final String NOT_FOUND_EXCEPTION_MESSAGE = "Discount card with id = %d not found";
    private static final String ALREADY_EXISTS_EXCEPTION_MESSAGE = "Discount card with number = %d already exists";

    private final Repository<Long, DiscountCard> discountCardRepository = new DiscountCardRepository();

    @Override
    public List<DiscountCardResponse> findAll(Integer pageSize, Integer pageNumber) {
        List<DiscountCard> discountCards = discountCardRepository.findAll(pageSize, pageNumber);

        return discountCards.stream()
                .map(DiscountCardMapper::mapToDiscountCardResponse)
                .toList();
    }

    @Override
    public DiscountCardResponse findById(Long id) {
        DiscountCard discountCard = discountCardRepository.findById(id)
                .orElseThrow(() -> new DiscountCardNotFoundException(String.format(NOT_FOUND_EXCEPTION_MESSAGE, id)));

        return DiscountCardMapper.mapToDiscountCardResponse(discountCard);
    }

    @Override
    public void save(DiscountCardRequest discountCardRequest) {
        DiscountCard discountCard = DiscountCardMapper.mapToDiscountCard(discountCardRequest);
        Integer discountCardNumber = discountCardRequest.getNumber();

        discountCardRepository.findByNumber(discountCardNumber)
                .ifPresent(foundDiscountCard -> {
                    throw new DiscountCardAlreadyExistsException(String.format(ALREADY_EXISTS_EXCEPTION_MESSAGE, discountCardNumber));
                });

        discountCardRepository.save(discountCard);
    }

    @Override
    public void update(Long id, DiscountCardRequest discountCardRequest) {
        DiscountCard discountCard = DiscountCardMapper.mapToDiscountCard(discountCardRequest);
        Integer discountCardNumber = discountCardRequest.getNumber();

        discountCardRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format(NOT_FOUND_EXCEPTION_MESSAGE, id)));
        discountCardRepository.findByNumber(discountCardNumber)
                .ifPresent(foundDiscountCard -> {
                    throw new DiscountCardAlreadyExistsException(String.format(ALREADY_EXISTS_EXCEPTION_MESSAGE, discountCardNumber));
                });

        discountCardRepository.update(id, discountCard);
    }

    @Override
    public void deleteById(Long id) {
        discountCardRepository.findById(id)
                .orElseThrow(() -> new DiscountCardNotFoundException(String.format(NOT_FOUND_EXCEPTION_MESSAGE, id)));

        discountCardRepository.deleteById(id);
    }
}
