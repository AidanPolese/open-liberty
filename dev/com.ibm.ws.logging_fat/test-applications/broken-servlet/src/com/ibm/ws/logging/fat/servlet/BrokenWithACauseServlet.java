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
@WebServlet("/BrokenWithACauseServlet")
public class BrokenWithACauseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrokenWithACauseServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        ReasonItAllWentWrongException reasonItAllWentWrongException = new ReasonItAllWentWrongException();
        throw new BrokenWithACauseException(reasonItAllWentWrongException);

    }

    static class BrokenWithACauseException extends RuntimeException {
        public BrokenWithACauseException(ReasonItAllWentWrongException reasonItAllWentWrongException) {
            super("arbitrary message", reasonItAllWentWrongException);
        }

        private static final long serialVersionUID = 1L;

    }

    static class ReasonItAllWentWrongException extends Exception {
        private static final long serialVersionUID = 1L;

    }

}
