package ru.clevertec.config;

import ru.clevertec.exception.DatabaseException;

import lombok.Getter;

@Getter
public class DataSource {

    private static DataSource instance;

    private final String url;
    private final String username;
    private final String password;

    private DataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DataSource getInstance(String url, String username, String password) {
        if (instance == null) {
            instance = new DataSource(url, username, password);
        }

        return instance;
    }

    public static DataSource getInstance() {
        if (instance == null) {
            throw new DatabaseException("Datasource is empty");
        }

        return instance;
    }
}
