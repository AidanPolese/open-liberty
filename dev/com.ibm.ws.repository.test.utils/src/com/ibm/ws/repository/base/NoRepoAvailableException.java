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
package com.ibm.ws.repository.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to indicate that we could not contact any servers.
 */
@SuppressWarnings("serial")
public class NoRepoAvailableException extends Exception {

    private class ServerProblem {
        private String url;
        private String apiKey;
        private Exception exception;
    }

    private List<ServerProblem> problems;

    public void add(String url, String apiKey, Exception cause) {
        ServerProblem p = new ServerProblem();
        p.url = url;
        p.apiKey = apiKey;
        p.exception = cause;

        if (problems == null) {
            problems = new ArrayList<ServerProblem>();
        }
        problems.add(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#getCause()
     */
    @Override
    public Throwable getCause() {
        if (problems != null) {
            for (ServerProblem problem : problems) {
                if (problem.exception != null) {
                    return problem.exception;
                }
            }
        }

        return super.getCause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        StringBuilder b = new StringBuilder();
        b.append("Could not find an available server:\n");

        if (problems == null) {
            // No problems were added -> no servers were tried
            b.append("There were no servers given to try\n");
        } else {
            // Add the list of failed servers to the message
            for (ServerProblem problem : problems) {
                b.append(problem.url);
                // Add the apiKey if one was provided
                if (problem.apiKey != null) {
                    b.append(" - ");
                    b.append(problem.apiKey);
                }

                if (problem.exception != null) {
                    b.append(": ");
                    b.append(problem.exception.toString());
                }
                b.append("\n");
            }
        }
        return b.toString();
    }
}
