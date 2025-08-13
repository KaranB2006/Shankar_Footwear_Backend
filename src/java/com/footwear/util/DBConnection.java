package com.footwear.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("org.postgresql.Driver");

            String host = "dpg-d2dhncndiees73d0de8g-a.oregon-postgres.render.com"; // External Hostname
            String port = "5432";
            String dbName = "footwear_db_5e30";
            String user = "footwear_db_5e30_user";
            String password = "0tD7xXzHVYMvJRBwS1Jjuz1s9K0br6FC";

            // Use SSL for Render external connection
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName + "?sslmode=require";

            con = DriverManager.getConnection(jdbcUrl, user, password);
            System.out.println("✅ Connected to PostgreSQL database");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ PostgreSQL JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to PostgreSQL");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("ErrorCode: " + e.getErrorCode());
        }

        return con;
    }
}
