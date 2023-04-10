package ru.clevertec.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<K, E> {
    List<E> findAll(Integer pageSize, Integer pageNumber);
    Optional<E> findById(K id);
    void save(E entity);
    void update(K id, E entity);
    void deleteById(K id);
}
