package com.footwear.servlet;

import com.footwear.util.DBConnection;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {

    // ✅ Railway frontend URL
    private static final String FRONTEND_URL = "https://shankar_footwear_frontend.up.railway.app";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // CORS headers for Railway frontend
        res.setHeader("Access-Control-Allow-Origin", FRONTEND_URL);
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        res.setHeader("Access-Control-Allow-Credentials", "true");

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                out.print("{\"status\":\"error\", \"message\":\"Database connection failed\"}");
                return;
            }

            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE email=? AND password=?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HttpSession session = req.getSession(true);
                session.setAttribute("userId", rs.getInt("id"));
                session.setAttribute("userName", rs.getString("name"));
                session.setAttribute("email", rs.getString("email"));

                String jsonResponse = String.format(
                    "{\"status\":\"success\", \"message\":\"Logged in\", \"name\":\"%s\"}",
                    rs.getString("name")
                );
                out.print(jsonResponse);
            } else {
                out.print("{\"status\":\"fail\", \"message\":\"Invalid credentials\"}");
            }

        } catch (Exception e) {
            e.printStackTrace(); // logs to server log
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Internal server error\"}");
        } finally {
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Preflight CORS for Railway frontend
        response.setHeader("Access-Control-Allow-Origin", FRONTEND_URL);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
