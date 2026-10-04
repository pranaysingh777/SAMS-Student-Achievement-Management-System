package com.sams.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            System.getenv("SAMS_DB_URL") != null
                    ? System.getenv("SAMS_DB_URL")
                    : "jdbc:mysql://localhost:3306/sams_db";

    private static final String USER =
            System.getenv("SAMS_DB_USER") != null
                    ? System.getenv("SAMS_DB_USER")
                    : "root";

    public static Connection getConnection() throws SQLException {

        String password = System.getenv("SAMS_DB_PASSWORD");

        if (password == null || password.isEmpty()) {
            throw new SQLException(
                    "Database password is not configured."
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC driver not found.", e
            );
        }

        return DriverManager.getConnection(
                URL,
                USER,
                password
        );
    }
}