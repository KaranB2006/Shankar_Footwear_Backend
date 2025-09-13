package com.footwear.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            // ✅ Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Railway MySQL connection details
            String host = "shuttle.proxy.rlwy.net";
            String port = "30349";
            String dbName = "railway";
            String user = "root";
            String password = "uMiSkSRuiPnDQYJvATLrRmJHNYNuzLcq"; // replace with actual Railway password

            // JDBC URL (with SSL & public key retrieval for Railway)
            String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                    + "?useSSL=true&requireSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

            // Connect
            con = DriverManager.getConnection(jdbcUrl, user, password);
            System.out.println("✅ Connected to MySQL database");
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
