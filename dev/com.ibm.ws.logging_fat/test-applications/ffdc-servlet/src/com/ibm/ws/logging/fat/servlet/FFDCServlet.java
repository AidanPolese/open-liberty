package com.ibm.ws.logging.fat.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FFDCServlet")
public class FFDCServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("Test Servlet to generate FFDC");
        
        String generateFFDC = request.getParameter("generateFFDC");

        if ((generateFFDC != null) && (generateFFDC.equalsIgnoreCase("true"))) {
            // Divide by zero, to get an ArithmeticException, which will generate an FFDC
            int i = 10 / 0;
        }
    }
}
