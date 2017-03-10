/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.diagnostics.java;

import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.TreeMap;

import com.ibm.wsspi.logging.Introspector;

public class EnvironmentVariables implements Introspector {
    @Override
    public String getIntrospectorName() {
        return "EnvironmentVariables";
    }

    @Override
    public String getIntrospectorDescription() {
        return "The native environment for the server process";
    }

    @Override
    public void introspect(PrintWriter writer) {
        // Put out a header before the information
        writer.println("Environment Variables");
        writer.println("---------------------");

        // Get the keys into a sorted map for display
        Map<String, String> env = new TreeMap<String, String>(getEnvironment());

        // Write the values
        for (Map.Entry<String, String> entry : env.entrySet()) {
            writer.print(entry.getKey());
            writer.print("=");
            writer.println(entry.getValue().replaceAll("\\\n", "<nl>"));
        }
    }

    /**
     * Get the system environment variables in a doPrivileged block.
     * 
     * @return the process environment variables
     */
    private Map<String, String> getEnvironment() {
        return AccessController.doPrivileged(new PrivilegedAction<Map<String, String>>() {
            @Override
            public Map<String, String> run() {
                return System.getenv();
            }
        });
    }
}
