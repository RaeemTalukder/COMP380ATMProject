import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class DatabaseManager {
    private final HikariDataSource ds;

    public DatabaseManager(HikariDataSource ds) {
        this.ds = ds;
    }

    public boolean verifyPIN(long cardNumber, int pin) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select * from accounts where card_num = ?");
            stmt.setLong(1, cardNumber);
            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                throw new IllegalArgumentException("Card number " + cardNumber + " is not valid.");
            }
            int actualPin = result.getInt("pin");
            return pin == actualPin;
        }
    }

    private int getPrimaryKey(long cardNumber) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select * from accounts where card_num = ?");
            stmt.setLong(1, cardNumber);
            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                throw new IllegalArgumentException("Card number " + cardNumber + " is not valid.");
            }
            return result.getInt("id");
        }
    }

    public double getBalance(long cardNumber) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select * from accounts where card_num = ?");
            stmt.setLong(1, cardNumber);
            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                throw new IllegalArgumentException("Card number " + cardNumber + " is not valid.");
            }
            return result.getDouble("balance");
        }
    }

    public void withdraw(long cardNumber, double amount) throws SQLException {
        Connection conn = ds.getConnection();

        try (conn) {
            PreparedStatement updateStmt = conn.prepareStatement
                    ("update accounts set balance = (balance - ?) where card_num = ?");
            updateStmt.setDouble(1, amount);
            updateStmt.setLong(2, cardNumber);
            updateStmt.executeUpdate();

            int primaryKey = getPrimaryKey(cardNumber);

            PreparedStatement insertStmt = conn.prepareStatement
                    ("insert into transactions (amount, account, transaction_type) values (?, ?, ?)");
            insertStmt.setDouble(1, amount);
            insertStmt.setInt(2, primaryKey);
            insertStmt.setString(3, "withdrawal");
            insertStmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
        }
    }

    public void deposit(long cardNumber, double amount) throws SQLException {
        Connection conn = ds.getConnection();

        try (conn) {
            PreparedStatement updateStmt = conn.prepareStatement
                    ("update accounts set balance = (balance + ?) where card_num = ?");
            updateStmt.setDouble(1, amount);
            updateStmt.setLong(2, cardNumber);
            updateStmt.executeUpdate();

            int primaryKey = getPrimaryKey(cardNumber);

            PreparedStatement insertStmt = conn.prepareStatement
                    ("insert into transactions (amount, account, transaction_type) values (?, ?, ?)");
            insertStmt.setDouble(1, amount);
            insertStmt.setInt(2, primaryKey);
            insertStmt.setString(3, "deposit");
            insertStmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
        }
    }

    public long getRandomCardNumber() throws SQLException {
        try (Connection conn = ds.getConnection()) {

            // Get the total number of accounts in the table
            PreparedStatement stmt = conn.prepareStatement("select count(*) from accounts");
            ResultSet result = stmt.executeQuery();
            result.next();
            int numAccounts = result.getInt(1);

            // Choose a random account
            Random random = new Random();
            int randomPrimaryKey = random.nextInt(numAccounts) + 1;

            // Return that random account's card number
            stmt = conn.prepareStatement("select * from accounts where id = ?");
            stmt.setInt(1, randomPrimaryKey);
            result = stmt.executeQuery();
            result.next();
            return result.getLong("card_num");
        }
    }

}
