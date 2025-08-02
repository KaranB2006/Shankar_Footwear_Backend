package com.footwear.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("org.postgresql.Driver");

            String host = "dpg-d26vo8m3jp1c73dnqjvg-a.oregon-postgres.render.com";
            String port = "5432";
            String dbName = "footwear_db_n4us";
            String user = "footwear_db_n4us_user";
            String password = "YXiPcjvJ6P4F8uE9V78eMufpWRXreZ7S";

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

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
