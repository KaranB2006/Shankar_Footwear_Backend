package com.footwear.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final boolean USE_ENV = true; // Set to false for local testing

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String host, port, dbName, user, password;

            if (USE_ENV) {
                host = System.getenv("MYSQLHOST");
                port = System.getenv("MYSQLPORT");
                dbName = System.getenv("MYSQLDATABASE");
                user = System.getenv("MYSQLUSER");
                password = System.getenv("MYSQLPASSWORD");

                if (host == null || port == null || dbName == null || user == null || password == null) {
                    throw new SQLException("❌ One or more environment variables are missing.");
                }
            } else {
                // Local DB fallback
                host = "localhost";
                port = "3306";
                dbName = "footwear";
                user = "root";
                password = ""; // your local DB password
            }

            String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                    + "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";

            con = DriverManager.getConnection(jdbcUrl, user, password);
            System.out.println("✅ Connected to MySQL");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to MySQL");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("ErrorCode: " + e.getErrorCode());
        }

        return con;
    }
}
