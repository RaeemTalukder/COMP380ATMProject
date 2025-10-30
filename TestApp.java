import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class TestApp {
    private static final String JDBC_URL =
            "jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?user=postgres.kjcrwhinbakxmxnzjswk&password=380ATMProject";

    public static void main(String[] args) throws SQLException {
        HikariDataSource ds = getDataSource();
        DatabaseManager manager = new DatabaseManager(ds);
        boolean pinVerification = manager.verifyPIN(1089248599745281L, 764);
        System.out.println("pinVerification = " + pinVerification);
    }

    public static HikariDataSource getDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(JDBC_URL);
        ds.setAutoCommit(false);
        return ds;
    }
}
