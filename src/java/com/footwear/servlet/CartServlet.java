package com.footwear.servlet;

import com.footwear.util.DBConnection;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

public class CartServlet extends HttpServlet {

    // ✅ Replace this with your Railway frontend URL
    private static final String FRONTEND_URL = "https://your-netlify-site.netlify.app";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":\"error\",\"message\":\"Please login first\"}");
            return;
        }

        // Enable CORS for Railway frontend
        res.setHeader("Access-Control-Allow-Origin", FRONTEND_URL);
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setContentType("application/json");

        PrintWriter out = res.getWriter();

        int userId = (int) session.getAttribute("userId");
        int productId = Integer.parseInt(req.getParameter("productId"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + ?"
            );
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setInt(4, quantity);
            ps.executeUpdate();

            out.print("{\"status\":\"success\"}");
        } catch (Exception e) {
            e.printStackTrace(); // logs to server
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // CORS for preflight requests
        res.setHeader("Access-Control-Allow-Origin", FRONTEND_URL);
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setStatus(HttpServletResponse.SC_OK);
    }
}
