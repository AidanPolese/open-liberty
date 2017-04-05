/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2004
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * 
 * This interface defines methods to retrieve Global Tran attributes as defined
 * in the deployment XML.
 */
public interface GlobalTranConfigData
{
    /**
     * @return int The component transaction timeout value.
     */
    public int getTransactionTimeout();

    /**
     * @return boolean The isSendWSAT value.
     */
    public boolean isSendWSAT();
}
