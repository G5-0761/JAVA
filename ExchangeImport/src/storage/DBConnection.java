package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// One place that knows how to connect to MySQL
public class DBConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/exchange_import_db";
    private static final String USER = "root";
    private static final String PASSWORD = "dhruvpathak123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}