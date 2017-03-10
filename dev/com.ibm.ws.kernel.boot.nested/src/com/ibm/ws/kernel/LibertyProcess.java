/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.kernel;

import java.util.Set;

/**
 * Simple service used to retrieve the command line arguments passed to the
 * runtime launcher. This service is registered in the OSGi service
 * registry. The code creating the service resides outside of the framework: it
 * is not a dynamic service, so direct access (rather than via a tracker) should
 * behave consistently. Calling code should still perform some defensive sanity
 * checking (like ensuring the returned service is not null, etc.).
 */
public interface LibertyProcess {
    /**
     * Return an array containing the arguments passed to the runtime on the
     * command line. A copy of the original argument list will be returned.
     * 
     * @return Array of String command line arguments; will always return a list
     *         (which may have 0 length).
     */
    public String[] getArgs();

    /**
     * Shutdown the OSGi framework. Results in the immediate (but orderly)
     * shutdown of the runtime and the OSGi framework, etc.
     */
    public void shutdown();

    /**
     * Creates the set of Java dumps specified in includedDumps.
     * 
     * @param includedDumps the set of dumps to create
     */
    public void createJavaDump(Set<String> includedDumps);

    /**
     * Creates a server dump, which will include any of the Java dumps specified in
     * includedDumps.
     * 
     * @param includedDumps the set of Java dumps to include in the server dump
     * 
     * @return the fully-qualified path to the server dump archive if the dump was
     *         successfully taken, or null otherwise
     */
    public String createServerDump(Set<String> includedDumps);
}
