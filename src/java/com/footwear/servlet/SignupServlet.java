package com.footwear.servlet;

import com.footwear.util.DBConnection;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class SignupServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // CORS headers
        res.setHeader("Access-Control-Allow-Origin", "https://shankar-footwear-frontend.onrender.com");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        res.setHeader("Access-Control-Allow-Credentials", "true");

        // Set response type
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        System.out.println("📥 Signup received: " + name + ", " + email);

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"status\":\"error\",\"message\":\"Database not connected\"}");
                return;
            }

            // Check if user exists
            PreparedStatement check = con.prepareStatement("SELECT * FROM users WHERE email = ?");
            check.setString(1, email);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                System.out.println("⚠️ User already exists: " + email);
                out.print("{\"status\":\"exists\", \"message\":\"User already exists\"}");
            } else {
                // Insert new user
                PreparedStatement ps = con.prepareStatement("INSERT INTO users(name, email, password) VALUES (?, ?, ?)");
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("✅ User inserted: " + email);
                    out.print("{\"status\":\"success\", \"message\":\"User registered successfully\"}");
                } else {
                    System.out.println("❌ Insert failed.");
                    res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"status\":\"fail\", \"message\":\"Failed to insert user\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Handle preflight request
        res.setHeader("Access-Control-Allow-Origin", "https://shankar-footwear-frontend.onrender.com");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setStatus(HttpServletResponse.SC_OK);
    }
}

