package main.java.com.rodasdev.sport.life.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    private static Connection connection;

    private DataBaseConnection() {
    }

    public static Connection getDataBaseConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    Crendentials.URL_DB,
                    Crendentials.USER_DB,
                    Crendentials.PASS_DB
            );
        }
        return connection;
    }

    public static Connection getConnection() throws SQLException {
        return getDataBaseConnection();
    }
}