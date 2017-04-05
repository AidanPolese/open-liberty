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
package com.ibm.ws.session;

/**
 * Provides extensions used by instances of <code>MemoryStore</code>.
 * 
 * @see com.ibm.ws.session.store.memory.MemoryStore
 */
public interface MemoryStoreHelper {

    /**
     * Method used to prepare the Thread to handle requests.
     * This should set an appropriate classpath on the thread.
     * 
     * @see com.ibm.ws.session.store.memory.MemoryStore#setThreadContext()
     */
    public void setThreadContext();
    
    /**
     * Method used to prepare the Thread to handle requests.
     * This should be called by the invalidation thread only
     * 
     * @see com.ibm.ws.session.store.memory.MemoryStore#setThreadContext()
     */
    public void setThreadContextDuringRunInvalidation();

    /**
     * Method used to change the Thread's properties back to what they were before
     * a setThreadContext was called.
     * 
     * @see com.ibm.ws.session.store.memory.MemoryStore#unsetThreadContext()
     */
    public void unsetThreadContext();
   

}
