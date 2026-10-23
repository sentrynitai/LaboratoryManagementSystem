package com.lms.dao;

import java.io.InputStream; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Centralized JDBC Database Connection Provider.
 * Demonstrates clean separation of concerns in the DAO layer of Java MVC.
 * Reads database parameters from cloud environment variables (Railway/Docker)
 * or falls back to db.properties / local defaults.
 */
public class DBConnection {

    private static String url = "jdbc:mysql://localhost:3306/laboratory_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "";
    private static String driver = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            // 1. Load configuration from db.properties if available on classpath
            try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    Properties prop = new Properties();
                    prop.load(input);
                    if (prop.getProperty("db.url") != null) url = prop.getProperty("db.url");
                    if (prop.getProperty("db.user") != null) user = prop.getProperty("db.user");
                    if (prop.getProperty("db.password") != null) password = prop.getProperty("db.password");
                    if (prop.getProperty("db.driver") != null) driver = prop.getProperty("db.driver");
                }
            } catch (Exception e) {
                System.err.println("Warning: Could not read db.properties, using defaults: " + e.getMessage());
            }

            // 2. Check for Railway or standard cloud environment variables (takes priority)
            String envHost = System.getenv("MYSQLHOST");
            String envPort = System.getenv("MYSQLPORT");
            String envDatabase = System.getenv("MYSQLDATABASE");
            String envUser = System.getenv("MYSQLUSER");
            String envPassword = System.getenv("MYSQLPASSWORD");

            if (envHost != null && !envHost.trim().isEmpty()) {
                String port = (envPort != null && !envPort.trim().isEmpty()) ? envPort : "3306";
                String db = (envDatabase != null && !envDatabase.trim().isEmpty()) ? envDatabase : "MYSQLDATABASE";
                url = "jdbc:mysql://" + envHost + ":" + port + "/" + db + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                if (envUser != null && !envUser.trim().isEmpty()) user = envUser;
                if (envPassword != null) password = envPassword;
                System.out.println("DBConnection: Configured using Railway MYSQL environment variables (host: " + envHost + ", db: " + db + ")");
            } else if (System.getenv("DB_URL") != null && !System.getenv("DB_URL").trim().isEmpty()) {
                url = System.getenv("DB_URL");
                if (System.getenv("DB_USER") != null) user = System.getenv("DB_USER");
                if (System.getenv("DB_PASSWORD") != null) password = System.getenv("DB_PASSWORD");
                System.out.println("DBConnection: Configured using DB_URL environment variable.");
            } else {
                System.out.println("DBConnection: Configured using db.properties (" + url + ")");
            }

            // Register MySQL JDBC Driver
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("Fatal: MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    /**
     * Obtains a new database connection from the driver manager.
     *
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Programmatically allows setting database credentials (useful during setup or testing).
     */
    public static void setCredentials(String dbUrl, String dbUser, String dbPassword) {
        if (dbUrl != null) url = dbUrl;
        if (dbUser != null) user = dbUser;
        if (dbPassword != null) password = dbPassword;
    }

    /**
     * Safely closes JDBC resources to prevent memory or connection leaks.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}
