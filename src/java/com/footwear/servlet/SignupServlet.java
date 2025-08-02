package com.footwear.servlet;

import com.footwear.util.DBConnection;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class SignupServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        
        res.setHeader("Access-Control-Allow-Origin", "https://shankar-footwear-frontend.onrender.com");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type , Authorization");
    
    
        res.setContentType("application/json");

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        
        PrintWriter out = res.getWriter();

        System.out.println("📥 Signup received: " + name + ", " + email);

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                out.print("{\"status\":\"error\",\"message\":\"DB not connected\"}");
                return;
            }

            PreparedStatement check = con.prepareStatement("SELECT * FROM users WHERE email=?");
            check.setString(1, email);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                System.out.println("⚠️ User already exists: " + email);
                out.print("{\"status\":\"exists\"}");
            } else {
                PreparedStatement ps = con.prepareStatement("INSERT INTO users(name, email, password) VALUES(?,?,?)");
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("✅ User inserted: " + email);
                    out.print("{\"status\":\"success\"}");
                } else {
                    System.out.println("❌ Insert failed.");
                    out.print("{\"status\":\"fail\"}");
                }
            }
        } catch (Exception e) {
    e.printStackTrace(); // ✅ Logs to server logs
    res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
}

    }
}
