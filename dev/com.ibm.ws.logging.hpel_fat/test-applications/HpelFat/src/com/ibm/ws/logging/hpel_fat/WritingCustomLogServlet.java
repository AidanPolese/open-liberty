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
package com.ibm.ws.logging.hpel_fat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.logging.hpel.handlers.MyCustomHandler;

/**
 * This is a servlet to write logs to a custom file
 */
public class WritingCustomLogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Logger logger = Logger.getLogger(getClass().getPackage()
                    .getName());

    @Override
    public void init() throws ServletException {
        logger.setLevel(Level.ALL);
        try {
            MyCustomHandler handler = new MyCustomHandler("customlog.log", 50000, 10, true);
            handler.setLevel(Level.ALL);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        } catch (Exception e) {
            logger.severe("FileHanler initilization failed:"
                          + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        logger.finest("Entering in to the doGet() method.");
        logger.info("doGet method of WritingCustomLogServlet called and now calling the doService");
        doService(request, response);
        logger.finest("Exiting from the doGet() method.");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        logger.finest("Entering in to the doPost() method.");
        logger.info("doPost method of WritingCustomLogServlet is called and now calling the doService");
        doService(request, response);
        logger.finest("Exiting from  the doPost() method.");
    }

    protected void doService(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, IOException {
        logger.finest("Entering in to the doService() method.");
        logger.fine("This is Fine message in doGet()");
        logger.finer("This is Finer message in doGet()");
        logger.config("This is config message in doGet()");

        PrintWriter pw = response.getWriter();
        pw.println("Servlet successfullly completed");

        this.logger.finest("Exiting from  doService() method.");
    }
}
