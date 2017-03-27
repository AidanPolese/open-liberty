/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.fat.servlet;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet with a dependency on JPA, running in a server with no jpa feature loaded. What could
 * possibly go wrong?
 */
@WebServlet("/MissingEntityManagerServlet")
public class MissingEntityManagerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "thiswontworkpu")
    private EntityManager em;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MissingEntityManagerServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("About to try and run a query on the entity manager we don't have.");
        String query = "SELECT f FROM ChocolateOrder f";
        Query q = em.createQuery(query);

        List<?> list = q.getResultList();
        response.getWriter().println("How did that work out?" + list);

    }

}
