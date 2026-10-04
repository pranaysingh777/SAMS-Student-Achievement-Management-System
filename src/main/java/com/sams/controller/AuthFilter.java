package com.sams.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/score-achievement",
        "/achievement-details"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        HttpSession session =
                httpRequest.getSession(false);

        if (session == null) {

            httpResponse.sendRedirect(
                    "login.html?error=login_required"
            );

            return;
        }

        String role =
                (String) session.getAttribute("role");

        String path =
                httpRequest.getServletPath();

        /*
         * Achievement details are needed by
         * the evaluation interface.
         *
         * Only evaluators should access them.
         */
        if ("/achievement-details".equals(path)
                && !"EVALUATOR".equals(role)) {

            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Only evaluators can access achievement details."
            );

            return;
        }

        chain.doFilter(
                request,
                response
        );
    }
}