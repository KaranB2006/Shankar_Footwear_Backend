package com.footwear.servlet;

import com.footwear.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class CheckoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // CORS settings for session support
        response.setHeader("Access-Control-Allow-Origin", "https://shankar-footwear-frontend.onrender.com");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"status\":\"unauthorized\", \"message\":\"User not logged in\"}");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try (Connection con = DBConnection.getConnection()) {

            // 1. Insert into orders
            String orderSql = "INSERT INTO orders (user_id) VALUES (?)";
            PreparedStatement orderStmt = con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            int rowsInserted = orderStmt.executeUpdate();

            if (rowsInserted == 0) {
                out.write("{\"status\":\"error\", \"message\":\"Order creation failed.\"}");
                return;
            }

            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                out.write("{\"status\":\"error\", \"message\":\"Failed to get order ID.\"}");
                return;
            }

            // 2. Fetch cart items for the user
            String fetchCart = "SELECT product_id, quantity FROM cart WHERE user_id = ?";
            PreparedStatement fetchStmt = con.prepareStatement(fetchCart);
            fetchStmt.setInt(1, userId);
            ResultSet cartRs = fetchStmt.executeQuery();

            // 3. Insert into order_items
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";
            PreparedStatement itemStmt = con.prepareStatement(itemSql);

            boolean hasItems = false;
            while (cartRs.next()) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, cartRs.getInt("product_id"));
                itemStmt.setInt(3, cartRs.getInt("quantity"));
                itemStmt.addBatch();
                hasItems = true;
            }

            if (hasItems) {
                itemStmt.executeBatch();
            } else {
                out.write("{\"status\":\"error\", \"message\":\"Cart is empty.\"}");
                return;
            }

            // 4. Clear cart
            String clearSql = "DELETE FROM cart WHERE user_id = ?";
            PreparedStatement clearStmt = con.prepareStatement(clearSql);
            clearStmt.setInt(1, userId);
            clearStmt.executeUpdate();

            out.write("{\"status\":\"success\"}");

        }  catch (Exception e) {
    e.printStackTrace(); // ✅ Logs to server logs
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
}

    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
