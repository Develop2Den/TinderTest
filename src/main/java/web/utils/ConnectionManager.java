package web.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

//    private static final String URL = "jdbc:postgresql://localhost:5432/fs_tinder";
//    private static final String USER = "postgres";
//    private static final String PASSWORD = "JavaLearn007";

    private static final String URL = "jdbc:postgresql://ep-purple-wildflower-a2bduym5.eu-central-1.aws.neon.tech/neonDB_tinder?sslmode=require";
    private static final String USER = "neonDB_tinder_owner";
    private static final String PASSWORD = "89XinrxabJAI";

    private ConnectionManager() { };

    static {
        loadDriver();
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
