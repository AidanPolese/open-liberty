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
package com.ibm.tx.remote;

/**
 *
 */
public class INTERNAL extends RuntimeException {

    /**
     * @param logicError
     * @param object
     */
    public INTERNAL(int logicError, Boolean completionStatus) {}

}
