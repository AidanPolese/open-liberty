/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.hpel;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class LRCServlet extends HttpServlet {

    /**  */
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        PrintWriter out = res.getWriter();
        res.setContentType("text/html");
        out.println("<HTML><HEAD><TITLE>LRC Servlet</TITLE></HEAD><BODY BGCOLOR=\"#FFFFEE\">");
        out.println("LRC Servlet");
        System.out.println("LRC Servlet doGet Method stars");
    }
}
