package com.sams.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/evaluate")
public class EvaluatorPageServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, ServletException {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    "login.html?error=login_required"
            );
            return;
        }

        String role =
                (String) session.getAttribute("role");

        if (!"EVALUATOR".equals(role)) {
            response.sendRedirect("dashboard.html");
            return;
        }

        request.getRequestDispatcher(
                "/score-achievement.html"
        ).forward(request, response);
    }
}