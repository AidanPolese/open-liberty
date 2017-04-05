/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.filemonitor;

import java.io.File;
import java.util.Collection;

import org.osgi.framework.BundleContext;

/**
 * Component that need to be notified by the SecurityFileMonitor when the files
 * they are interested in are modified or recreated need to implement this interface
 * and pass themselves to a new instance of SecurityFileMonitor.
 */
public interface FileBasedActionable {

    /**
     * Callback method to be invoked by the file monitor
     * to instruct the implementation to perform its action.
     * 
     * @param modifiedFiles
     */
    void performFileBasedAction(Collection<File> modifiedFiles);

    /**
     * Returns the implementation's BundleContext.
     */
    BundleContext getBundleContext();

}
