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
package com.ibm.wsspi.collective.plugins;

import java.io.IOException;

/**
 * This interface contains actions that can be done in a collective environment, such as archive deployment.
 * 
 * @ibm-spi
 */
public interface CollectiveExecutor {

    /**
     * Deploy an archive to the collective.
     * 
     * @param taskID a token to be used together with {@link TaskStorage} to fetch the task information
     * @param async a boolean specifying if this task should be executed in asynchronous mode
     * @throws IOException
     */
    public void deployArchive(String taskID, boolean async) throws IOException;

    /**
     * Deletes a file location in the collective.
     * 
     * @param taskID a token to be used together with {@link TaskStorage} to fetch the task information
     * @param async a boolean specifying if this task should be executed in asynchronous mode
     * @throws IOException
     */
    public void deleteFile(String taskID, boolean async) throws IOException;

}
