package com.footwear.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CreateOrderServlet extends HttpServlet {

    private static final String APP_ID = "YOUR_CASHFREE_APP_ID";
    private static final String SECRET_KEY = "YOUR_CASHFREE_SECRET_KEY";
    private static final String CASHFREE_ORDER_URL = "https://sandbox.cashfree.com/pg/orders"; // live URL in production

    // ✅ Railway frontend URL
    private static final String FRONTEND_URL = "https://your-netlify-site.netlify.app";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // CORS headers for Railway frontend
        response.setHeader("Access-Control-Allow-Origin", FRONTEND_URL);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        // Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"status\":\"error\",\"message\":\"User not logged in\"}");
            return;
        }

        try {
            // Read request body
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            if (sb.length() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"Empty request body\"}");
                return;
            }

            // Dummy email for now; can use session if available
            String customerEmail = "testuser@example.com";

            // Random order ID and amount
            String orderId = "order_" + System.currentTimeMillis();
            int amount = 500; // ₹500

            // JSON payload for Cashfree
            String payload = "{"
                    + "\"order_id\":\"" + orderId + "\","
                    + "\"order_amount\":" + amount + ","
                    + "\"order_currency\":\"INR\","
                    + "\"customer_details\":{"
                    +     "\"customer_id\":\"" + session.getAttribute("userId") + "\","
                    +     "\"customer_email\":\"" + customerEmail + "\""
                    + "}"
                    + "}";

            // Connect to Cashfree
            URL url = new URL(CASHFREE_ORDER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-client-id", APP_ID);
            conn.setRequestProperty("x-client-secret", SECRET_KEY);

            // Send payload
            OutputStream os = conn.getOutputStream();
            os.write(payload.getBytes(StandardCharsets.UTF_8));
            os.flush();

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseStr = new StringBuilder();
            while ((line = in.readLine()) != null) {
                responseStr.append(line);
            }
            in.close();

            out.print(responseStr.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Preflight CORS for Railway frontend
        response.setHeader("Access-Control-Allow-Origin", FRONTEND_URL);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
