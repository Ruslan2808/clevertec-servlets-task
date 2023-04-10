package ru.clevertec.repository;

import java.util.Optional;

public interface Repository<K, E> extends CrudRepository<K, E> {
    Optional<E> findByNumber(Integer number);
}
