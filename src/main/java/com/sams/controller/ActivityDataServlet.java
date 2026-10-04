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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/activity-data")
public class ActivityDataServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Only allow the dashboard's supported ranges.
        int days = 7;

        String daysParameter = request.getParameter("days");

        if (daysParameter != null) {
            try {
                int requestedDays = Integer.parseInt(daysParameter);

                if (requestedDays == 7 ||
                    requestedDays == 30 ||
                    requestedDays == 90) {

                    days = requestedDays;
                }

            } catch (NumberFormatException ignored) {
                // Keep the default of 7 days.
            }
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<Integer> counts = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            counts.add(0);
        }

        String sql =
                "SELECT DATE(submitted_at) AS achievement_date, " +
                "COUNT(*) AS achievement_count " +
                "FROM achievements " +
                "WHERE submitted_at >= ? " +
                "AND submitted_at < ? " +
                "GROUP BY DATE(submitted_at) " +
                "ORDER BY achievement_date";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    startDate.toString() + " 00:00:00"
            );

            statement.setString(
                    2,
                    endDate.plusDays(1).toString() + " 00:00:00"
            );

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    LocalDate achievementDate =
                            result.getDate("achievement_date")
                                  .toLocalDate();

                    int index =
                            (int) (achievementDate.toEpochDay()
                            - startDate.toEpochDay());

                    if (index >= 0 && index < days) {
                        counts.set(
                                index,
                                result.getInt("achievement_count")
                        );
                    }
                }
            }

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("MMM d");

            StringBuilder labels = new StringBuilder();

            for (int i = 0; i < days; i++) {

                if (i > 0) {
                    labels.append(",");
                }

                LocalDate date = startDate.plusDays(i);

                labels.append(jsonString(
                        date.format(formatter)
                ));
            }

            StringBuilder countJson = new StringBuilder();

            for (int i = 0; i < counts.size(); i++) {

                if (i > 0) {
                    countJson.append(",");
                }

                countJson.append(counts.get(i));
            }

            String json =
                    "{"
                    + "\"days\":" + days + ","
                    + "\"labels\":[" + labels + "],"
                    + "\"counts\":[" + countJson + "]"
                    + "}";

            try (PrintWriter out = response.getWriter()) {
                out.print(json);
            }

        } catch (Exception e) {

            getServletContext().log(
                    "Failed to load activity data.",
                    e
            );

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            try (PrintWriter out = response.getWriter()) {
                out.print(
                        "{\"error\":\"Unable to load activity data.\"}"
                );
            }
        }
    }

    private String jsonString(String value) {

        if (value == null) {
            return "null";
        }

        return "\"" +
                value.replace("\\", "\\\\")
                     .replace("\"", "\\\"") +
                "\"";
    }
}