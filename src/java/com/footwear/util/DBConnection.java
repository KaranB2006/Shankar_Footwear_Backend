package com.footwear.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // ✅ Railway DB credentials
            String host = "yamanote.proxy.rlwy.net";
            String port = "40331";
            String dbName = "railway";
            String user = "root";
            String password = "zkRbxMieyKBxEGfREDhThzUnTrSqmxv";

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
