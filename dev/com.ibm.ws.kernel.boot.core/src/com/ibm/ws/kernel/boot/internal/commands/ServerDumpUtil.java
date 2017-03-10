/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.internal.commands;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Util for the dump command
 */
public class ServerDumpUtil {

    /**
     *  In general find another way to do what you are trying, this is meant as a 
     *  VERY VERY last resort and agreed to by Gary.
     */
    public static boolean isZos() {

        String os = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("os.name");
            }
        });

        return os != null && (os.equalsIgnoreCase("OS/390") || os.equalsIgnoreCase("z/OS"));
    }
}
