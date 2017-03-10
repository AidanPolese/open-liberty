/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.launch.service;

public interface ClientRunner {
    /**
     * A registered service of this interface will run application's main() method in a client module. If no ClientRunner is found
     * after the framework is ready (see {@link FrameworkReady}), then an error will be issued, and the client process will exit.
     * 
     * If an exception occurs while a registered service is executing main(), ReturnCode.CLIENT_RUNNER_EXCEPTION, or (int) 35
     * is returned as the exit code.
     */
    void run();
}
