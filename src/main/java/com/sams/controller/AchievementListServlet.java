
package com.sams.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sams.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/achievements")
public class AchievementListServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String sql =
                "SELECT achievement_id, title, description, " +
                "status, submitted_at, evidence_file_name, " +
                "quality_score " +
                "FROM achievements " +
                "ORDER BY submitted_at DESC";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery();
             PrintWriter out =
                     response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");

            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' " +
                    "content='width=device-width, initial-scale=1.0'>");

            out.println("<title>SAMS | Achievements</title>");

            out.println("<style>");

            out.println("* { box-sizing: border-box; }");

            out.println("body {");
            out.println("margin: 0;");
            out.println("font-family: Arial, sans-serif;");
            out.println("background: #F7F7F0;");
            out.println("color: #173F35;");
            out.println("}");

            out.println(".topbar {");
            out.println("background: #173F35;");
            out.println("color: white;");
            out.println("padding: 18px 32px;");
            out.println("display: flex;");
            out.println("justify-content: space-between;");
            out.println("align-items: center;");
            out.println("}");

            out.println(".brand {");
            out.println("font-size: 22px;");
            out.println("font-weight: 700;");
            out.println("}");

            out.println(".nav a {");
            out.println("color: white;");
            out.println("text-decoration: none;");
            out.println("margin-left: 20px;");
            out.println("font-size: 14px;");
            out.println("}");

            out.println(".nav a:hover {");
            out.println("text-decoration: underline;");
            out.println("}");

            out.println(".container {");
            out.println("max-width: 1200px;");
            out.println("margin: 40px auto;");
            out.println("padding: 0 24px;");
            out.println("}");

            out.println(".header {");
            out.println("margin-bottom: 24px;");
            out.println("}");

            out.println(".header h1 {");
            out.println("margin: 0 0 8px 0;");
            out.println("font-size: 32px;");
            out.println("}");

            out.println(".header p {");
            out.println("margin: 0;");
            out.println("color: #65756F;");
            out.println("}");

            out.println(".table-card {");
            out.println("background: white;");
            out.println("border-radius: 18px;");
            out.println("padding: 10px;");
            out.println("box-shadow: 0 8px 30px rgba(23,63,53,0.08);");
            out.println("overflow-x: auto;");
            out.println("}");

            out.println("table {");
            out.println("width: 100%;");
            out.println("border-collapse: collapse;");
            out.println("}");

            out.println("th {");
            out.println("text-align: left;");
            out.println("padding: 16px;");
            out.println("font-size: 12px;");
            out.println("text-transform: uppercase;");
            out.println("letter-spacing: 0.08em;");
            out.println("color: #65756F;");
            out.println("border-bottom: 1px solid #E7ECE8;");
            out.println("}");

            out.println("td {");
            out.println("padding: 18px 16px;");
            out.println("border-bottom: 1px solid #EEF2EF;");
            out.println("vertical-align: middle;");
            out.println("}");

            out.println("tr:last-child td {");
            out.println("border-bottom: none;");
            out.println("}");

            out.println(".title {");
            out.println("font-weight: 700;");
            out.println("color: #173F35;");
            out.println("}");

            out.println(".description {");
            out.println("max-width: 280px;");
            out.println("color: #65756F;");
            out.println("line-height: 1.5;");
            out.println("}");

            out.println(".status {");
            out.println("display: inline-block;");
            out.println("padding: 6px 10px;");
            out.println("border-radius: 999px;");
            out.println("background: #EEF4F0;");
            out.println("color: #28604F;");
            out.println("font-size: 12px;");
            out.println("font-weight: 700;");
            out.println("}");

            out.println(".score {");
            out.println("font-weight: 800;");
            out.println("font-size: 16px;");
            out.println("color: #173F35;");
            out.println("}");

            out.println(".unscored {");
            out.println("color: #9AA7A1;");
            out.println("font-size: 13px;");
            out.println("}");

            out.println(".action {");
            out.println("display: inline-block;");
            out.println("padding: 9px 13px;");
            out.println("border-radius: 9px;");
            out.println("background: #173F35;");
            out.println("color: white;");
            out.println("text-decoration: none;");
            out.println("font-size: 13px;");
            out.println("font-weight: 700;");
            out.println("}");

            out.println(".action:hover {");
            out.println("background: #28604F;");
            out.println("}");

            out.println(".evidence {");
            out.println("color: #28604F;");
            out.println("font-weight: 700;");
            out.println("text-decoration: none;");
            out.println("font-size: 13px;");
            out.println("}");

            out.println(".evidence:hover {");
            out.println("text-decoration: underline;");
            out.println("}");

            out.println(".empty {");
            out.println("text-align: center;");
            out.println("padding: 50px;");
            out.println("color: #65756F;");
            out.println("}");

            out.println("</style>");
            out.println("</head>");

            out.println("<body>");

            out.println("<div class='topbar'>");

            out.println("<div class='brand'>SAMS</div>");

            out.println("<div class='nav'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("<a href='dashboard.html'>Dashboard</a>");
            out.println("<a href='achievement-form.html'>Submit Achievement</a>");
            out.println("</div>");

            out.println("</div>");

            out.println("<div class='container'>");

            out.println("<div class='header'>");
            out.println("<h1>Achievement Records</h1>");
            out.println("<p>Review submitted achievements, evidence, and quality evaluations.</p>");
            out.println("</div>");

            out.println("<div class='table-card'>");

            out.println("<table>");

            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>Achievement</th>");
            out.println("<th>Description</th>");
            out.println("<th>Status</th>");
            out.println("<th>Score</th>");
            out.println("<th>Evidence</th>");
            out.println("<th>Action</th>");
            out.println("</tr>");

            boolean hasRecords = false;

            while (resultSet.next()) {

                hasRecords = true;

                int achievementId =
                        resultSet.getInt("achievement_id");

                String title =
                        resultSet.getString("title");

                String description =
                        resultSet.getString("description");

                String status =
                        resultSet.getString("status");

                String evidenceFileName =
                        resultSet.getString("evidence_file_name");

                int qualityScore =
                        resultSet.getInt("quality_score");

                boolean hasScore =
                        !resultSet.wasNull();

                out.println("<tr>");

                out.println("<td>"
                        + achievementId
                        + "</td>");

                out.println("<td class='title'>"
                        + escapeHtml(title)
                        + "</td>");

                out.println("<td class='description'>"
                        + escapeHtml(description)
                        + "</td>");

                out.println("<td>");
                out.println("<span class='status'>"
                        + escapeHtml(status)
                        + "</span>");
                out.println("</td>");

                out.println("<td>");

                if (hasScore) {

                    out.println(
                            "<span class='score'>" +
                            qualityScore +
                            "/100</span>"
                    );

                } else {

                    out.println(
                            "<span class='unscored'>" +
                            "Not evaluated" +
                            "</span>"
                    );
                }

                out.println("</td>");

                out.println("<td>");

                if (evidenceFileName != null
                        && !evidenceFileName.trim().isEmpty()) {

                    out.println(
                            "<a class='evidence' " +
                            "href='evidence-file?id=" +
                            achievementId +
                            "' target='_blank'>" +
                            "View Evidence" +
                            "</a>"
                    );

                } else {

                    out.println(
                            "<span class='unscored'>" +
                            "No file" +
                            "</span>"
                    );
                }

                out.println("</td>");

                out.println("<td>");

                out.println(
                "<a class='action' " +
                "href='evaluate?id=" +
                achievementId +
                "'>" +
                (hasScore ? "Review Score" : "Evaluate") +
                "</a>"
                );

                out.println("</td>");

                out.println("</tr>");
            }

            if (!hasRecords) {

                out.println(
                        "<tr>" +
                        "<td colspan='7' class='empty'>" +
                        "No achievements submitted yet." +
                        "</td>" +
                        "</tr>"
                );
            }

            out.println("</table>");

            out.println("</div>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to retrieve achievements.",
                    e
            );
        }
    }

    private String escapeHtml(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

