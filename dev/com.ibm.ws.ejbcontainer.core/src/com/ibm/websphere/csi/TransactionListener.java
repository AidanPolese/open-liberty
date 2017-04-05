/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2003
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * Receives tx notifications from the Container. PM provides the implementation class.
 */
public interface TransactionListener {

    public void afterBegin();

    public void afterCompletion(int isCommit); // d160445

    public void beforeCompletion();

    /**
     * Set whether the LTC is being driven mid-ActivitySession
     * or the UOW of work is completing.
     * 
     * @param isCompleting is defined as follows:
     *            <UL>
     *            <LI>true - UOW is completing.</LI>
     *            <LI>false - ActivitySession is executing a mid session checkpoint/reset.</LI>
     *            </UL>
     */
    public void setCompleting(boolean isCompleting);

}