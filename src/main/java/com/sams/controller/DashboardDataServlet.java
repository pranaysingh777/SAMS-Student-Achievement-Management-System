package com.sams.controller;

import com.sams.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/dashboard-data")
public class DashboardDataServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int total = 0;
        int pending = 0;
        int approved = 0;

        StringBuilder recent = new StringBuilder();
        StringBuilder topAchievements = new StringBuilder();

        boolean firstRecent = true;
        boolean firstTop = true;

        try (Connection connection = DBConnection.getConnection()) {

            // ==============================
            // DASHBOARD STATISTICS
            // ==============================

            String statsSql =
                    "SELECT COUNT(*) AS total, " +
                    "COALESCE(SUM(CASE WHEN status = 'PENDING' " +
                    "THEN 1 ELSE 0 END), 0) AS pending, " +
                    "COALESCE(SUM(CASE WHEN status = 'APPROVED' " +
                    "THEN 1 ELSE 0 END), 0) AS approved " +
                    "FROM achievements";

            try (PreparedStatement statement =
                         connection.prepareStatement(statsSql);
                 ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    total = result.getInt("total");
                    pending = result.getInt("pending");
                    approved = result.getInt("approved");
                }
            }

            // ==============================
            // RECENT ACHIEVEMENTS
            // ==============================

            String recentSql =
                    "SELECT achievement_id, title, description, " +
                    "status, submitted_at " +
                    "FROM achievements " +
                    "ORDER BY submitted_at DESC, achievement_id DESC " +
                    "LIMIT 5";

            try (PreparedStatement statement =
                         connection.prepareStatement(recentSql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    if (!firstRecent) {
                        recent.append(",");
                    }

                    firstRecent = false;

                    recent.append("{")
                            .append("\"id\":")
                            .append(result.getInt("achievement_id"))
                            .append(",\"title\":")
                            .append(jsonString(
                                    result.getString("title")))
                            .append(",\"description\":")
                            .append(jsonString(
                                    result.getString("description")))
                            .append(",\"status\":")
                            .append(jsonString(
                                    result.getString("status")))
                            .append(",\"submittedAt\":")
                            .append(jsonString(
                                    result.getString("submitted_at")))
                            .append("}");
                }
            }

            // ==============================
            // TOP ACHIEVEMENTS
            // ==============================

            String topSql =
                    "SELECT achievement_id, title, quality_score " +
                    "FROM achievements " +
                    "WHERE quality_score IS NOT NULL " +
                    "ORDER BY quality_score DESC, " +
                    "submitted_at DESC, achievement_id DESC " +
                    "LIMIT 3";

            try (PreparedStatement statement =
                         connection.prepareStatement(topSql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    if (!firstTop) {
                        topAchievements.append(",");
                    }

                    firstTop = false;

                    topAchievements.append("{")
                            .append("\"id\":")
                            .append(result.getInt("achievement_id"))
                            .append(",\"title\":")
                            .append(jsonString(
                                    result.getString("title")))
                            .append(",\"qualityScore\":")
                            .append(result.getInt("quality_score"))
                            .append("}");
                }
            }

            // ==============================
            // FINAL JSON
            // ==============================

            String json =
                    "{"
                    + "\"total\":" + total + ","
                    + "\"pending\":" + pending + ","
                    + "\"approved\":" + approved + ","
                    + "\"recent\":[" + recent + "],"
                    + "\"topAchievements\":[" +
                        topAchievements +
                    "]"
                    + "}";

            try (PrintWriter out = response.getWriter()) {
                out.print(json);
            }

        } catch (SQLException e) {

            getServletContext().log(
                    "Failed to load SAMS dashboard data.", e);

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            try (PrintWriter out = response.getWriter()) {
                out.print(
                        "{\"error\":\"Unable to load dashboard data.\"}");
            }
        }
    }

    // Safely encode database text as a JSON string.
    private String jsonString(String value) {

        if (value == null) {
            return "null";
        }

        StringBuilder escaped =
                new StringBuilder("\"");

        for (char c : value.toCharArray()) {

            switch (c) {

                case '"':
                    escaped.append("\\\"");
                    break;

                case '\\':
                    escaped.append("\\\\");
                    break;

                case '\b':
                    escaped.append("\\b");
                    break;

                case '\f':
                    escaped.append("\\f");
                    break;

                case '\n':
                    escaped.append("\\n");
                    break;

                case '\r':
                    escaped.append("\\r");
                    break;

                case '\t':
                    escaped.append("\\t");
                    break;

                default:

                    if (c < 0x20) {
                        escaped.append(
                                String.format(
                                        "\\u%04x",
                                        (int) c));
                    } else {
                        escaped.append(c);
                    }
            }
        }

        return escaped.append('"').toString();
    }
}