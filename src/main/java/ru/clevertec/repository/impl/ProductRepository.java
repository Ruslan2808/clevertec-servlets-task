package ru.clevertec.repository.impl;

import ru.clevertec.config.DataSource;
import ru.clevertec.entity.Product;
import ru.clevertec.exception.DatabaseException;
import ru.clevertec.repository.CrudRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository implements CrudRepository<Long, Product> {

    private final DataSource dataSource = DataSource.getInstance();

    @Override
    public List<Product> findAll(Integer pageSize, Integer pageNumber) {
        final String selectAllQuery = "SELECT id, name, price, is_promotional FROM product LIMIT ? OFFSET ?;";
        List<Product> products = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(selectAllQuery)
        ) {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, pageSize * (pageNumber - 1));

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Product product = Product.builder()
                        .id(resultSet.getLong(1))
                        .name(resultSet.getString(2))
                        .price(resultSet.getDouble(3))
                        .isPromotional(resultSet.getBoolean(4))
                        .build();

                products.add(product);
            }

            return products;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        final String selectByIdQuery = "SELECT id, name, price, is_promotional FROM product WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(selectByIdQuery)
        ) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Product product = Product.builder()
                        .id(resultSet.getLong(1))
                        .name(resultSet.getString(2))
                        .price(resultSet.getDouble(3))
                        .isPromotional(resultSet.getBoolean(4))
                        .build();

                return Optional.of(product);
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void save(Product product) {
        final String insertQuery = "INSERT INTO product (name, price, is_promotional) VALUES (?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)
        ) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setBoolean(3, product.getIsPromotional());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void update(Long id, Product product) {
        final String updateQuery = "UPDATE product SET name = ?, price = ?, is_promotional = ? WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)
        ) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setBoolean(3, product.getIsPromotional());
            preparedStatement.setLong(4, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        final String deleteByIdQuery = "DELETE FROM product WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(deleteByIdQuery)
        ) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
