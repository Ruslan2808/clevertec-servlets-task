package ru.clevertec.repository.impl;

import ru.clevertec.config.DataSource;
import ru.clevertec.entity.DiscountCard;
import ru.clevertec.exception.DatabaseException;
import ru.clevertec.repository.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiscountCardRepository implements Repository<Long, DiscountCard> {

    private final DataSource dataSource = DataSource.getInstance();

    @Override
    public List<DiscountCard> findAll(Integer pageSize, Integer pageNumber) {
        final String selectAllQuery = "SELECT id, number, discount FROM discount_card LIMIT ? OFFSET ?;";
        List<DiscountCard> discountCards = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(selectAllQuery)
        ) {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, pageSize * (pageNumber - 1));

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                DiscountCard discountCard = DiscountCard.builder()
                        .id(resultSet.getLong(1))
                        .number(resultSet.getInt(2))
                        .discount(resultSet.getDouble(3))
                        .build();

                discountCards.add(discountCard);
            }

            return discountCards;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public Optional<DiscountCard> findById(Long id) {
        final String selectByIdQuery = "SELECT id, number, discount FROM discount_card WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(selectByIdQuery)
        ) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            return mapToOptionalDiscountCard(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public Optional<DiscountCard> findByNumber(Integer number) {
        final String selectByNumberQuery = "SELECT id, number, discount FROM discount_card WHERE number = ?;";

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(selectByNumberQuery)
        ) {
            preparedStatement.setInt(1, number);

            ResultSet resultSet = preparedStatement.executeQuery();

            return mapToOptionalDiscountCard(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void save(DiscountCard discountCard) {
        final String insertQuery = "INSERT INTO discount_card (number, discount) VALUES (?, ?);";

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)
        ) {
            preparedStatement.setInt(1, discountCard.getNumber());
            preparedStatement.setDouble(2, discountCard.getDiscount());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void update(Long id, DiscountCard discountCard) {
        final String updateQuery = "UPDATE discount_card SET number = ?, discount = ? WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)
        ) {
            preparedStatement.setInt(1, discountCard.getNumber());
            preparedStatement.setDouble(2, discountCard.getDiscount());
            preparedStatement.setLong(3, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        final String deleteByIdQuery = "DELETE FROM discount_card WHERE id = ?;";

        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(deleteByIdQuery)
        ) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private Optional<DiscountCard> mapToOptionalDiscountCard(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            DiscountCard discountCard = DiscountCard.builder()
                    .id(resultSet.getLong(1))
                    .number(resultSet.getInt(2))
                    .discount(resultSet.getDouble(3))
                    .build();

            return Optional.of(discountCard);
        }

        return Optional.empty();
    }
}
