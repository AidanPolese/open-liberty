package com.ibm.ws.logging.fat.servlet;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet which prints an exception.
 */
@WebServlet("/IBMCodeAtTopExceptionPrintingServlet")
public class IBMCodeAtTopExceptionPrintingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public IBMCodeAtTopExceptionPrintingServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        response.getWriter().println("Howdy! This servlet is working just fine, except for all the bits that are deliberately broken.");

        // In the absence of the jndi feature, this lookup shouldn't go well
        try {
            InitialContext ctx = new InitialContext();
            ctx.lookup("something/That/Does/Not/Exist");
        } catch (NamingException e) {
            // Print the stack trace, and see what happens
            e.printStackTrace();
        }

        response.getWriter().println("There should be an exception in your logs.");

    }
}
