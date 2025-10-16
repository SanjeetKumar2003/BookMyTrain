import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bookmytrain?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Siya@2003";

    static {
        // Ensure tables exist when the class is loaded
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // users table
            String users = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) UNIQUE NOT NULL,"
                    + "password VARCHAR(100) NOT NULL,"
                    + "fullname VARCHAR(100),"
                    + "contact VARCHAR(20)"
                    + ")";
            stmt.execute(users);

            // trains table
            String trains = "CREATE TABLE IF NOT EXISTS trains ("
                    + "train_id INT PRIMARY KEY,"
                    + "train_name VARCHAR(100),"
                    + "source VARCHAR(50),"
                    + "destination VARCHAR(50),"
                    + "fare DOUBLE,"
                    + "seats INT"
                    + ")";
            stmt.execute(trains);

            // tickets table
            String tickets = "CREATE TABLE IF NOT EXISTS tickets ("
                    + "ticket_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL,"
                    + "train_id INT NOT NULL,"
                    + "seats_booked INT NOT NULL,"
                    + "booked_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (train_id) REFERENCES trains(train_id)"
                    + ")";
            stmt.execute(tickets);

        } catch (SQLException e) {
            System.out.println("Error initializing database schema: " + e.getMessage());
            // continue — errors printed for user awareness
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
