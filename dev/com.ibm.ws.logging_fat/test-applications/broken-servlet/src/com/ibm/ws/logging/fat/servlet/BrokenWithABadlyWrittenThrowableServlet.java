package com.ibm.ws.logging.fat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet which throws an exception. The getStackTrace() method on the exception returns null.
 */
@WebServlet("/BrokenWithABadlyWrittenThrowableServlet")
public class BrokenWithABadlyWrittenThrowableServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrokenWithABadlyWrittenThrowableServlet() {
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
        throw new BadlyWrittenException();

    }

    static class BadlyWrittenException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        @Override
        public StackTraceElement[] getStackTrace() {
            // Can our logging code handle this?
            return null;
        }

    }

}
