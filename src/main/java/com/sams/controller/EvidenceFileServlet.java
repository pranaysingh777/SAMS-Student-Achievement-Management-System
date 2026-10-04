package com.sams.controller;

import com.sams.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/evidence-file")
public class EvidenceFileServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String idParameter =
                request.getParameter("id");

        if (idParameter == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Achievement ID is required."
            );
            return;
        }

        int achievementId;

        try {
            achievementId = Integer.parseInt(idParameter);
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid achievement ID."
            );
            return;
        }

        String storedFileName = null;
        String originalFileName = null;

        String sql =
                "SELECT evidence_file, evidence_file_name " +
                "FROM achievements " +
                "WHERE achievement_id = ?";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, achievementId);

            try (ResultSet result =
                         statement.executeQuery()) {

                if (result.next()) {

                    storedFileName =
                            result.getString("evidence_file");

                    originalFileName =
                            result.getString(
                                    "evidence_file_name");
                }
            }

        } catch (SQLException e) {

            getServletContext().log(
                    "Failed to find evidence file.", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to find evidence file."
            );

            return;
        }

        if (storedFileName == null
                || storedFileName.trim().isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "No evidence file found."
            );

            return;
        }

        Path uploadDirectory = Paths.get(
                System.getProperty("catalina.base"),
                "sams-uploads"
        );

        Path file = uploadDirectory.resolve(storedFileName);

        if (!Files.exists(file)
                || !Files.isRegularFile(file)) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Evidence file is missing."
            );

            return;
        }

        String contentType =
                Files.probeContentType(file);

        if (contentType == null) {
            contentType =
                    "application/octet-stream";
        }

        response.setContentType(contentType);

        if (originalFileName != null
                && !originalFileName.trim().isEmpty()) {

            String safeFileName =
                    originalFileName
                            .replace("\"", "")
                            .replace("\r", "")
                            .replace("\n", "");

            response.setHeader(
                    "Content-Disposition",
                    "inline; filename=\"" +
                    safeFileName +
                    "\""
            );
        }

        response.setContentLengthLong(
                Files.size(file)
        );

        try (InputStream input =
                     Files.newInputStream(file);
             OutputStream output =
                     response.getOutputStream()) {

            byte[] buffer = new byte[8192];

            int bytesRead;

            while ((bytesRead =
                    input.read(buffer)) != -1) {

                output.write(
                        buffer,
                        0,
                        bytesRead
                );
            }
        }
    }
}