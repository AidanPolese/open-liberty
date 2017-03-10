/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.filemonitor;

import java.util.Collection;

import com.ibm.wsspi.kernel.filemonitor.FileMonitor;

public interface FileNotification {
    /**
     * This is the notification method. Only {@link FileMonitor}s that
     * have external monitoring enabled will receive these notifications
     * and then only for the files they are monitoring.
     * 
     * @param createdFiles the absolute paths of any created files
     * @param modifiedFiles the absolute paths of any modified files
     * @param deletedFiles the absolute paths of any deleted files
     */
    void notifyFileChanges(Collection<String> createdFiles, Collection<String> modifiedFiles, Collection<String> deletedFiles);

}
