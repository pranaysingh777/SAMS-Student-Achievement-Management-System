package com.sams.controller;

import com.sams.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

@Override
protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
) throws IOException {

    request.setCharacterEncoding("UTF-8");

    String username =
            request.getParameter("username");

    String password =
            request.getParameter("password");

    String selectedRole =
            request.getParameter("role");

    if (username == null || password == null
            || selectedRole == null
            || username.trim().isEmpty()
            || password.trim().isEmpty()
            || selectedRole.trim().isEmpty()) {

        response.sendRedirect(
                "login.html?error=missing"
        );
        return;
    }

    String sql =
            "SELECT user_id, username, role " +
            "FROM users " +
            "WHERE username = ? AND password = ?";

    try (
            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setString(
                1,
                username.trim()
        );

        statement.setString(
                2,
                password
        );

        try (
                ResultSet result =
                        statement.executeQuery()
        ) {

            if (!result.next()) {

                response.sendRedirect(
                        "login.html?error=invalid"
                );

                return;
            }

            int userId =
                    result.getInt("user_id");

            String loggedInUsername =
                    result.getString("username");

            String actualRole =
                    result.getString("role");

            /*
             * Verify that the selected login type
             * matches the user's actual database role.
             */
            if (!actualRole.equalsIgnoreCase(
                    selectedRole.trim()
            )) {

                response.sendRedirect(
                        "login.html?error=role"
                );

                return;
            }

            HttpSession session =
                    request.getSession(true);

            session.setAttribute(
                    "userId",
                    userId
            );

            session.setAttribute(
                    "username",
                    loggedInUsername
            );

            session.setAttribute(
                    "role",
                    actualRole
            );

            session.setMaxInactiveInterval(
                    30 * 60
            );

            response.sendRedirect(
                    "dashboard.html"
            );
        }

    } catch (SQLException e) {

        getServletContext().log(
                "Login failed.",
                e
        );

        response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Unable to process login."
        );
    }
}


}
