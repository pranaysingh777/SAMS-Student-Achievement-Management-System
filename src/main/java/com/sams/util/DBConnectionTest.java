package com.sams.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnectionTest {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT DATABASE(), @@port, @@hostname")) {

            if (resultSet.next()) {
                System.out.println("Database: "
                        + resultSet.getString(1));

                System.out.println("Port: "
                        + resultSet.getInt(2));

                System.out.println("Host: "
                        + resultSet.getString(3));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}