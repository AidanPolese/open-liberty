/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.wsspi.webcontainer.servlet;

import java.util.Map;

/**
 * ITransferContextService extension. 
 */
public interface ITransferContextServiceExt extends ITransferContextService{
    
    /**
     * Informs service implementers that contextual work has been initiated prior to being queued for
     * execution.
     * 
     * @param m The map holding default and implementer specific context information.
     */
    public void preProcessWorkState(Map<String, Object> m);
    
    /**
     * Informs service implementers that the asynchronous request has completed.
     * 
     * @param m The map holding default and implementer specific context information.
     */
    public void completeState(Map<String, Object> m);
}
