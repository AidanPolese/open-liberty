package com.ibm.ws.logging.fat.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/*")
@SuppressWarnings("serial")
public class LoggerServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LoggerServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("Hello world!");
        // Use severe, which is higher than AUDIT, to ensure this message would
        // normally show up in console.log if output wasn't disabled.
        logger.severe("Hello world!");
    }
}
