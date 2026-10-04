
package com.sams.controller;

import com.sams.util.DBConnection;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
@WebServlet("/submit-achievement")
public class AchievementServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, jakarta.servlet.ServletException {

        request.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String impact = request.getParameter("impact");
        String difficulty = request.getParameter("difficulty");

        Part evidencePart =
                request.getPart("evidenceFile");

        if (title == null || title.trim().isEmpty()
                || description == null
                || description.trim().isEmpty()
                || evidencePart == null
                || evidencePart.getSize() == 0) {

            sendError(
                    response,
                    "Title, description, and evidence file are required."
            );
            return;
        }

        title = title.trim();
        description = description.trim();
        category = category == null
                ? "Other"
                : category.trim();

        impact = impact == null
                ? ""
                : impact.trim();

        difficulty = difficulty == null
                ? "MEDIUM"
                : difficulty.trim();

        if (title.length() > 100) {
            sendError(response, "Title is too long.");
            return;
        }

        if (description.length() > 1000) {
            sendError(response, "Description is too long.");
            return;
        }

        if (impact.length() > 1000) {
            sendError(response, "Impact description is too long.");
            return;
        }

        String originalFileName =
                Paths.get(
                        evidencePart.getSubmittedFileName()
                ).getFileName().toString();

        String extension =
                getExtension(originalFileName);

        if (!isAllowedExtension(extension)) {

            sendError(
                    response,
                    "Invalid file type. Allowed: PDF, PNG, JPG, JPEG, WEBP."
            );

            return;
        }

        String contentType =
                evidencePart.getContentType();

        if (!isAllowedContentType(contentType)) {

            sendError(
                    response,
                    "Unsupported evidence file type."
            );

            return;
        }

        String storedFileName =
                UUID.randomUUID() + extension;

        Path uploadDirectory =
                Paths.get(
                        System.getProperty("catalina.base"),
                        "sams-uploads"
                );

        Files.createDirectories(uploadDirectory);

        Path storedFile =
                uploadDirectory.resolve(storedFileName);

        try {

            evidencePart.write(
                    storedFile.toString()
            );

            String sql =
                    "INSERT INTO achievements " +
                    "(title, description, status, category, evidence, " +
                    "impact, difficulty, evidence_file, evidence_file_name) " +
                    "VALUES (?, ?, 'PENDING', ?, ?, ?, ?, ?, ?)";

            try (
                    Connection connection =
                            DBConnection.getConnection();

                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setString(1, title);
                statement.setString(2, description);
                statement.setString(3, category);
                statement.setString(4, "");
                statement.setString(5, impact);
                statement.setString(6, difficulty);
                statement.setString(7, storedFileName);
                statement.setString(8, originalFileName);

                statement.executeUpdate();
            }

            sendSuccess(response, title);

        } catch (SQLException | IOException e) {

            Files.deleteIfExists(storedFile);

            getServletContext().log(
                    "Failed to save achievement.",
                    e
            );

            sendError(
                    response,
                    "Unable to save achievement. Please try again."
            );
        }
    }

    private String getExtension(String fileName) {

        int dot =
                fileName.lastIndexOf('.');

        if (dot == -1) {
            return "";
        }

        return fileName
                .substring(dot)
                .toLowerCase();
    }

    private boolean isAllowedExtension(
            String extension) {

        return extension.equals(".pdf")
                || extension.equals(".png")
                || extension.equals(".jpg")
                || extension.equals(".jpeg")
                || extension.equals(".webp");
    }

    private boolean isAllowedContentType(
            String contentType) {

        if (contentType == null) {
            return false;
        }

        return contentType.equalsIgnoreCase(
                    "application/pdf")
                || contentType.equalsIgnoreCase(
                    "image/png")
                || contentType.equalsIgnoreCase(
                    "image/jpeg")
                || contentType.equalsIgnoreCase(
                    "image/webp");
    }

    private void sendSuccess(
            HttpServletResponse response,
            String title
    ) throws IOException {

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        try (PrintWriter out =
                     response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");

            out.println("<head>");

            out.println(
                    "<meta charset='UTF-8'>"
            );

            out.println(
                    "<meta name='viewport' " +
                    "content='width=device-width, initial-scale=1.0'>"
            );

            out.println(
                    "<title>SAMS | Achievement Submitted</title>"
            );

            out.println("<style>");

            out.println(
                    ":root{" +
                    "--ivory:#F7F7F0;" +
                    "--forest:#244B3B;" +
                    "--sage:#A8BDA5;" +
                    "--gold:#D5AD61;" +
                    "--charcoal:#26352D;" +
                    "--muted:#718078;" +
                    "--line:#DDE5DA;" +
                    "}"
            );

            out.println(
                    "*{box-sizing:border-box;}"
            );

            out.println(
                    "body{" +
                    "margin:0;" +
                    "background:var(--ivory);" +
                    "color:var(--charcoal);" +
                    "font-family:Arial,sans-serif;" +
                    "}"
            );

            out.println(
                    "nav{" +
                    "height:64px;" +
                    "padding:0 7%;" +
                    "display:flex;" +
                    "align-items:center;" +
                    "justify-content:space-between;" +
                    "background:white;" +
                    "border-bottom:1px solid var(--line);" +
                    "}"
            );

            out.println(
                    ".brand{" +
                    "color:var(--forest);" +
                    "font-size:18px;" +
                    "font-weight:700;" +
                    "text-decoration:none;" +
                    "}"
            );

            out.println(
                    ".back{" +
                    "color:var(--forest);" +
                    "text-decoration:none;" +
                    "font-size:13px;" +
                    "font-weight:600;" +
                    "}"
            );

            out.println(
                    "main{" +
                    "max-width:620px;" +
                    "margin:80px auto;" +
                    "padding:0 20px;" +
                    "}"
            );

            out.println(
                    ".card{" +
                    "background:white;" +
                    "border:1px solid var(--line);" +
                    "border-radius:20px;" +
                    "padding:42px;" +
                    "text-align:center;" +
                    "box-shadow:0 14px 40px rgba(36,75,59,.07);" +
                    "}"
            );

            out.println(
                    ".icon{" +
                    "width:64px;" +
                    "height:64px;" +
                    "margin:0 auto 22px;" +
                    "border-radius:50%;" +
                    "background:#EAF2E8;" +
                    "color:var(--forest);" +
                    "display:flex;" +
                    "align-items:center;" +
                    "justify-content:center;" +
                    "font-size:30px;" +
                    "font-weight:700;" +
                    "}"
            );

            out.println(
                    ".eyebrow{" +
                    "color:var(--gold);" +
                    "font-size:11px;" +
                    "font-weight:700;" +
                    "letter-spacing:1.5px;" +
                    "text-transform:uppercase;" +
                    "}"
            );

            out.println(
                    "h1{" +
                    "margin:8px 0 12px;" +
                    "color:var(--forest);" +
                    "font-size:30px;" +
                    "}"
            );

            out.println(
                    ".title{" +
                    "margin:0 auto 10px;" +
                    "color:var(--charcoal);" +
                    "font-size:16px;" +
                    "font-weight:700;" +
                    "}"
            );

            out.println(
                    ".message{" +
                    "margin:0 auto 24px;" +
                    "color:var(--muted);" +
                    "font-size:14px;" +
                    "line-height:1.6;" +
                    "}"
            );

            out.println(
                    ".status{" +
                    "display:inline-block;" +
                    "padding:7px 13px;" +
                    "border-radius:999px;" +
                    "background:#F5F0E4;" +
                    "color:#8A6A2D;" +
                    "font-size:12px;" +
                    "font-weight:700;" +
                    "margin-bottom:28px;" +
                    "}"
            );

            out.println(
                    ".actions{" +
                    "display:flex;" +
                    "gap:10px;" +
                    "justify-content:center;" +
                    "flex-wrap:wrap;" +
                    "}"
            );

            out.println(
                    ".button{" +
                    "display:inline-block;" +
                    "padding:12px 18px;" +
                    "border-radius:10px;" +
                    "text-decoration:none;" +
                    "font-size:13px;" +
                    "font-weight:700;" +
                    "}"
            );

            out.println(
                    ".primary{" +
                    "background:var(--forest);" +
                    "color:white;" +
                    "}"
            );

            out.println(
                    ".secondary{" +
                    "background:#F1F6EF;" +
                    "color:var(--forest);" +
                    "}"
            );

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<nav>");

            out.println(
                    "<a class='brand' href='index.html'>SAMS</a>"
            );

            out.println(
                    "<a class='back' href='dashboard.html'>" +
                    "Dashboard" +
                    "</a>"
            );

            out.println("</nav>");

            out.println("<main>");

            out.println("<div class='card'>");

            out.println(
                    "<div class='icon'>✓</div>"
            );

            out.println(
                    "<div class='eyebrow'>" +
                    "Submission Complete" +
                    "</div>"
            );

            out.println(
                    "<h1>Achievement submitted.</h1>"
            );

            out.println(
                    "<p class='title'>" +
                    escapeHtml(title) +
                    "</p>"
            );

            out.println(
                    "<p class='message'>" +
                    "Your achievement and evidence have been " +
                    "securely recorded in SAMS and are ready " +
                    "for evaluation." +
                    "</p>"
            );

            out.println(
                    "<span class='status'>" +
                    "PENDING REVIEW" +
                    "</span>"
            );

            out.println("<div class='actions'>");

            out.println(
                    "<a class='button primary' " +
                    "href='dashboard.html'>" +
                    "View Dashboard" +
                    "</a>"
            );

            out.println(
                    "<a class='button secondary' " +
                    "href='achievements'>" +
                    "View Achievements" +
                    "</a>"
            );

            out.println("</div>");

            out.println("</div>");

            out.println("</main>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    private void sendError(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
        );

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        try (PrintWriter out =
                     response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");

            out.println("<head>");

            out.println(
                    "<meta charset='UTF-8'>"
            );

            out.println(
                    "<meta name='viewport' " +
                    "content='width=device-width, initial-scale=1.0'>"
            );

            out.println(
                    "<title>SAMS | Submission Error</title>"
            );

            out.println("<style>");

            out.println(
                    ":root{" +
                    "--ivory:#F7F7F0;" +
                    "--forest:#244B3B;" +
                    "--gold:#D5AD61;" +
                    "--charcoal:#26352D;" +
                    "--muted:#718078;" +
                    "--line:#DDE5DA;" +
                    "}"
            );

            out.println(
                    "*{box-sizing:border-box;}"
            );

            out.println(
                    "body{" +
                    "margin:0;" +
                    "background:var(--ivory);" +
                    "color:var(--charcoal);" +
                    "font-family:Arial,sans-serif;" +
                    "}"
            );

            out.println(
                    "nav{" +
                    "height:64px;" +
                    "padding:0 7%;" +
                    "display:flex;" +
                    "align-items:center;" +
                    "justify-content:space-between;" +
                    "background:white;" +
                    "border-bottom:1px solid var(--line);" +
                    "}"
            );

            out.println(
                    ".brand{" +
                    "color:var(--forest);" +
                    "font-size:18px;" +
                    "font-weight:700;" +
                    "text-decoration:none;" +
                    "}"
            );

            out.println(
                    ".back{" +
                    "color:var(--forest);" +
                    "text-decoration:none;" +
                    "font-size:13px;" +
                    "font-weight:600;" +
                    "}"
            );

            out.println(
                    "main{" +
                    "max-width:620px;" +
                    "margin:80px auto;" +
                    "padding:0 20px;" +
                    "}"
            );

            out.println(
                    ".card{" +
                    "background:white;" +
                    "border:1px solid var(--line);" +
                    "border-radius:20px;" +
                    "padding:42px;" +
                    "text-align:center;" +
                    "box-shadow:0 14px 40px rgba(36,75,59,.07);" +
                    "}"
            );

            out.println(
                    ".icon{" +
                    "width:64px;" +
                    "height:64px;" +
                    "margin:0 auto 22px;" +
                    "border-radius:50%;" +
                    "background:#FFF1ED;" +
                    "color:#A45145;" +
                    "display:flex;" +
                    "align-items:center;" +
                    "justify-content:center;" +
                    "font-size:28px;" +
                    "font-weight:700;" +
                    "}"
            );

            out.println(
                    ".eyebrow{" +
                    "color:var(--gold);" +
                    "font-size:11px;" +
                    "font-weight:700;" +
                    "letter-spacing:1.5px;" +
                    "text-transform:uppercase;" +
                    "}"
            );

            out.println(
                    "h1{" +
                    "margin:8px 0 12px;" +
                    "color:var(--forest);" +
                    "font-size:30px;" +
                    "}"
            );

            out.println(
                    ".message{" +
                    "margin:0 auto 28px;" +
                    "color:var(--muted);" +
                    "font-size:14px;" +
                    "line-height:1.6;" +
                    "}"
            );

            out.println(
                    ".button{" +
                    "display:inline-block;" +
                    "padding:12px 18px;" +
                    "border-radius:10px;" +
                    "background:var(--forest);" +
                    "color:white;" +
                    "text-decoration:none;" +
                    "font-size:13px;" +
                    "font-weight:700;" +
                    "}"
            );

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<nav>");

            out.println(
                    "<a class='brand' href='index.html'>SAMS</a>"
            );

            out.println(
                    "<a class='back' href='dashboard.html'>" +
                    "Dashboard" +
                    "</a>"
            );

            out.println("</nav>");

            out.println("<main>");

            out.println("<div class='card'>");

            out.println(
                    "<div class='icon'>!</div>"
            );

            out.println(
                    "<div class='eyebrow'>Submission Issue</div>"
            );

            out.println(
                    "<h1>Submission could not be completed.</h1>"
            );

            out.println(
                    "<p class='message'>" +
                    escapeHtml(message) +
                    "</p>"
            );

            out.println(
                    "<a class='button' " +
                    "href='achievement-form.html'>" +
                    "Return to Submission" +
                    "</a>"
            );

            out.println("</div>");

            out.println("</main>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

