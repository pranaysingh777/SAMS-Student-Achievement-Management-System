
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

@WebServlet("/achievement-details")
public class AchievementDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParameter =
                request.getParameter("id");

        if (idParameter == null) {
            sendError(response, "Achievement ID is required.");
            return;
        }

        int achievementId;

        try {
            achievementId =
                    Integer.parseInt(idParameter);
        } catch (NumberFormatException e) {
            sendError(response, "Invalid achievement ID.");
            return;
        }

        String sql =
                "SELECT achievement_id, title, description, " +
                "category, status, evidence_file_name, " +
                "quality_score " +
                "FROM achievements " +
                "WHERE achievement_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, achievementId);

            try (ResultSet result =
                         statement.executeQuery()) {

                if (!result.next()) {
                    response.setStatus(
                            HttpServletResponse.SC_NOT_FOUND
                    );

                    sendError(
                            response,
                            "Achievement not found."
                    );

                    return;
                }

                PrintWriter out =
                        response.getWriter();

                out.print("{");

                out.print("\"id\":"
                        + result.getInt("achievement_id")
                        + ",");

                out.print("\"title\":\""
                        + escapeJson(
                            result.getString("title"))
                        + "\",");

                out.print("\"description\":\""
                        + escapeJson(
                            result.getString("description"))
                        + "\",");

                out.print("\"category\":\""
                        + escapeJson(
                            result.getString("category"))
                        + "\",");

                out.print("\"status\":\""
                        + escapeJson(
                            result.getString("status"))
                        + "\",");

                String evidence =
                        result.getString(
                                "evidence_file_name");

                if (evidence == null) {
                    evidence = "";
                }

                out.print("\"evidenceFile\":\""
                        + escapeJson(evidence)
                        + "\",");

                int qualityScore =
                        result.getInt("quality_score");

                if (result.wasNull()) {
                    out.print("\"qualityScore\":null");
                } else {
                    out.print("\"qualityScore\":"
                            + qualityScore);
                }

                out.print("}");
            }

        } catch (SQLException e) {

            getServletContext().log(
                    "Failed to retrieve achievement details.",
                    e
            );

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            sendError(
                    response,
                    "Unable to retrieve achievement."
            );
        }
    }

    private void sendError(
            HttpServletResponse response,
            String message)
            throws IOException {

        response.getWriter().print(
                "{\"error\":\"" +
                escapeJson(message) +
                "\"}"
        );
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}

