package db;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 * Handles the single shared JDBC connection to the MySQL database.
 * Update URL / USER / PASSWORD to match your local MySQL setup.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/evbikeshowroom";
    private static final String USER = "root";
    private static final String PASSWORD = "Root@123"; 

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "MySQL JDBC Driver not found.\nMake sure mysql-connector-j-x.x.x.jar is on the classpath.",
                    "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to database:\n" + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }
}
