/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.internal;

import java.io.IOException;
import java.util.Set;

import com.ibm.ws.kernel.boot.LaunchException;

/**
 * Interface between the framework launcher entry point and the framework
 * manager entry point, which are loaded by separate class loaders.
 */
public interface LauncherDelegate {

    /**
     * This method delegates the actual framework launch to the constructed
     * FrameworkManager. Before it does that, it initializes any configured
     * LogProviders (Tr and FFDC), and finds and constructs a
     * FrameworkConfigurator based on values present in the provided
     * BootstrapConfiguration.
     * 
     * @throws LaunchException
     *             If the framework can not be launched; this is propagated from
     *             the delegated call to the FrameworkManager
     * @throws RuntimeException
     *             If an unexpected runtime exception occurred while launching
     *             the framework; this may be propagated from the delegated call
     *             to the FrameworkManager. Uncaught Throwables are also mapped
     *             to RuntimeExceptions and are then re-thrown to the caller.
     */
    void launchFramework();

    /**
     * Wait for the framework to become fully started.
     * 
     * @return true if the framework was started successfully
     * @throws InterruptedException
     *             If the thread is interrupted before the framework launch
     *             status is determined
     */
    boolean waitForReady() throws InterruptedException;

    /**
     * Shutdown the framework
     * 
     * @throws InterruptedException
     *             If the thread is interrupted before the framework stop
     * @return true if the framework was shutdown successfully
     */
    boolean shutdown() throws InterruptedException;

    /**
     * Query feature info.
     * 
     * Used for minify.
     * 
     * @param osRequest contains any os filtering request information.
     * @return set of absolute paths representing all files used by configured features for the server
     */
    Set<String> queryFeatureInformation(String osRequest) throws IOException;

    /**
     * Query feature names.
     * 
     * @return set of feature names of the configured features for the server
     */
    Set<String> queryFeatureNames();
}
