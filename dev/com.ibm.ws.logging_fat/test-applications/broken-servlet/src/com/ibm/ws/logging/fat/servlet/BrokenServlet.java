package com.ibm.ws.logging.fat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet which throws an exception.
 */
@WebServlet("/BrokenServlet")
public class BrokenServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrokenServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        // Whoops, we seem to have a problem! Oh dear, how unexpected!
        throw new SpecialBrokenException();

    }

    static class SpecialBrokenException extends RuntimeException {

        private static final long serialVersionUID = 1L;

    }

}
