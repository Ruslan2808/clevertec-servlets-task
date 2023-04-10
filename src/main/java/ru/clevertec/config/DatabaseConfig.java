package ru.clevertec.config;

import ru.clevertec.exception.DatabaseException;
import ru.clevertec.exception.SQLFileIOException;
import ru.clevertec.exception.SQLNotFoundException;

import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Map;
import java.util.Objects;

@WebListener
public class DatabaseConfig implements ServletContextListener {

    private static final String SQL_FILE_NOT_FOUND_EXCEPTION_MESSAGE = "Sql file %s not found";
    private static final String PATH_INIT_SQL_FILE = "/db/init.sql";
    private static final String PATH_TEST_DATA_SQL_FILE = "/db/test_data.sql";
    private static final String PATH_APPLICATION_CONFIG_FILE = "/application.yml";

    private Map<String, String> datasourceProperties;
    private DataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initializeDataSource();
        String driver = datasourceProperties.get("driver-class-name");

        try {
            Class.forName(driver);

            String createTablesQuery = getQueryFromSqlFile(PATH_INIT_SQL_FILE);
            String insertTestDataQuery = getQueryFromSqlFile(PATH_TEST_DATA_SQL_FILE);

            executeSqlQuery(createTablesQuery);
            executeSqlQuery(insertTestDataQuery);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(e.getMessage());
        } catch (IOException e) {
            throw new SQLFileIOException(e.getMessage());
        }
    }

    private String getQueryFromSqlFile(String pathSqlFile) throws IOException {
        InputStream inputStreamQuery = DatabaseConfig.class.getClassLoader().getResourceAsStream(pathSqlFile);
        if (Objects.isNull(inputStreamQuery)) {
            throw new SQLNotFoundException(SQL_FILE_NOT_FOUND_EXCEPTION_MESSAGE);
        }

        return new String(inputStreamQuery.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void executeSqlQuery(String sqlQuery) {
        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private void initializeDataSource() {
        Yaml yaml = new Yaml();
        InputStream inputStream = DatabaseConfig.class.getResourceAsStream(PATH_APPLICATION_CONFIG_FILE);
        Map<String, Map<String, String>> configProperties = yaml.load(inputStream);

        datasourceProperties = configProperties.get("datasource");
        String url = datasourceProperties.get("url");
        String username = datasourceProperties.get("username");
        String password = datasourceProperties.get("password");

        dataSource = DataSource.getInstance(url, username, password);
    }
}
