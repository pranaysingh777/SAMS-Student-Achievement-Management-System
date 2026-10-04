package com.sams.controller;

import com.sams.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/score-achievement")
public class ScoreAchievementServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        request.setCharacterEncoding("UTF-8");

        try {

            int achievementId =
                    Integer.parseInt(
                            request.getParameter("id"));

            int impactScore =
                    Integer.parseInt(
                            request.getParameter("impactScore"));

            int evidenceScore =
                    Integer.parseInt(
                            request.getParameter("evidenceScore"));

            int difficultyScore =
                    Integer.parseInt(
                            request.getParameter("difficultyScore"));

            int originalityScore =
                    Integer.parseInt(
                            request.getParameter("originalityScore"));

            if (impactScore < 0 || impactScore > 30
                    || evidenceScore < 0 || evidenceScore > 30
                    || difficultyScore < 0 || difficultyScore > 20
                    || originalityScore < 0 || originalityScore > 20) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid score range."
                );

                return;
            }

            int qualityScore =
                    impactScore
                    + evidenceScore
                    + difficultyScore
                    + originalityScore;

            String sql =
                    "UPDATE achievements SET " +
                    "impact_score = ?, " +
                    "evidence_score = ?, " +
                    "difficulty_score = ?, " +
                    "originality_score = ?, " +
                    "quality_score = ? " +
                    "WHERE achievement_id = ?";

            try (Connection connection =
                         DBConnection.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, impactScore);
                statement.setInt(2, evidenceScore);
                statement.setInt(3, difficultyScore);
                statement.setInt(4, originalityScore);
                statement.setInt(5, qualityScore);
                statement.setInt(6, achievementId);

                int updated =
                        statement.executeUpdate();

                if (updated == 0) {

                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "Achievement not found."
                    );

                    return;
                }
            }

            response.sendRedirect("achievements");

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid scoring values."
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Failed to save achievement score.",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to save achievement score."
            );
        }
    }
}